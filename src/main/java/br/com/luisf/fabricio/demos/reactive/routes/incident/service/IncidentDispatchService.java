package br.com.luisf.fabricio.demos.reactive.routes.incident.service;

import br.com.luisf.fabricio.demos.reactive.routes.incident.api.CreateIncidentRequest;
import br.com.luisf.fabricio.demos.reactive.routes.incident.api.IncidentLiveSignal;
import br.com.luisf.fabricio.demos.reactive.routes.incident.api.IncidentResponse;
import br.com.luisf.fabricio.demos.reactive.routes.incident.api.IncidentSummaryResponse;
import br.com.luisf.fabricio.demos.reactive.routes.incident.api.PriorityBoardResponse;
import br.com.luisf.fabricio.demos.reactive.routes.incident.domain.Incident;
import br.com.luisf.fabricio.demos.reactive.routes.incident.domain.IncidentStatus;
import br.com.luisf.fabricio.demos.reactive.routes.incident.domain.Severity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class IncidentDispatchService {

    private static final Comparator<Incident> INCIDENT_PRIORITY = Comparator
            .comparingInt((Incident incident) -> incident.severity().priority())
            .reversed()
            .thenComparing(Incident::openedAt);

    private final CopyOnWriteArrayList<Incident> incidents = new CopyOnWriteArrayList<>();
    private final AtomicInteger sequence = new AtomicInteger(1000);
    private final IncidentMapper mapper;

    public IncidentDispatchService(IncidentMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    void seed() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        incidents.add(new Incident("INC-1001", Severity.CRITICAL, "payments-api",
                "Aumento sustentado de erros 5xx no gateway de pagamentos", "platform-sre",
                IncidentStatus.OPEN, now.minusMinutes(28), now.minusMinutes(28)));
        incidents.add(new Incident("INC-1002", Severity.HIGH, "checkout-web",
                "Latência acima do SLO no fluxo de compra", "frontend-ops",
                IncidentStatus.TRIAGED, now.minusMinutes(21), now.minusMinutes(15)));
        incidents.add(new Incident("INC-1003", Severity.MEDIUM, "notification-worker",
                "Fila de eventos acumulada após pico de reprocessamento", "messaging-team",
                IncidentStatus.OPEN, now.minusMinutes(17), now.minusMinutes(17)));
        incidents.add(new Incident("INC-1004", Severity.CRITICAL, "identity-service",
                "Falha intermitente na autenticação de operadores", "security-runtime",
                IncidentStatus.OPEN, now.minusMinutes(11), now.minusMinutes(11)));
        sequence.set(1004);
    }

    public Uni<IncidentResponse> create(CreateIncidentRequest request) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Incident incident = new Incident(
                "INC-" + sequence.incrementAndGet(),
                request.severity(),
                request.affectedService().strip(),
                request.summary().strip(),
                request.owner().strip(),
                IncidentStatus.OPEN,
                now,
                now);
        incidents.add(incident);
        return Uni.createFrom().item(mapper.toResponse(incident));
    }

    public Uni<PriorityBoardResponse> priorityBoard() {
        return Uni.createFrom().item(this::priorityBoardSnapshot);
    }

    public Uni<IncidentSummaryResponse> summary() {
        return Uni.createFrom().item(this::summarySnapshot);
    }

    public Multi<IncidentLiveSignal> liveSignals() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transform(index -> new IncidentLiveSignal(
                        Math.toIntExact(index) + 1,
                        "incident-dispatch",
                        OffsetDateTime.now(ZoneOffset.UTC),
                        summarySnapshot(),
                        priorityBoardSnapshot().queue().stream().findFirst().orElse(null)))
                .select().first(5);
    }

    public int totalIncidents() {
        return incidents.size();
    }

    private PriorityBoardResponse priorityBoardSnapshot() {
        List<IncidentResponse> queue = incidents.stream()
                .filter(incident -> incident.status() != IncidentStatus.RESOLVED)
                .sorted(INCIDENT_PRIORITY)
                .map(mapper::toResponse)
                .toList();
        return new PriorityBoardResponse(queue.size(), queue);
    }

    private IncidentSummaryResponse summarySnapshot() {
        List<Incident> snapshot = List.copyOf(incidents);
        Map<Severity, Long> totalsBySeverity = new EnumMap<>(Severity.class);
        for (Severity severity : Severity.values()) {
            totalsBySeverity.put(severity, snapshot.stream().filter(incident -> incident.severity() == severity).count());
        }
        List<IncidentResponse> criticalOpenIncidents = snapshot.stream()
                .filter(incident -> incident.severity() == Severity.CRITICAL)
                .filter(incident -> incident.status() != IncidentStatus.RESOLVED)
                .sorted(INCIDENT_PRIORITY)
                .map(mapper::toResponse)
                .toList();
        long openIncidents = snapshot.stream().filter(incident -> incident.status() != IncidentStatus.RESOLVED).count();
        return new IncidentSummaryResponse(snapshot.size(), (int) openIncidents, totalsBySeverity, criticalOpenIncidents);
    }
}
