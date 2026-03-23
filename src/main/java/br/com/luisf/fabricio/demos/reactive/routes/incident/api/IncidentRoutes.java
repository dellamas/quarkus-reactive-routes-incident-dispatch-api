package br.com.luisf.fabricio.demos.reactive.routes.incident.api;

import br.com.luisf.fabricio.demos.reactive.routes.incident.service.IncidentDispatchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@ApplicationScoped
@RouteBase(path = "incidents", produces = "application/json")
public class IncidentRoutes {

    private final IncidentDispatchService service;
    private final ObjectMapper objectMapper;

    public IncidentRoutes(IncidentDispatchService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @Route(path = "", methods = Route.HttpMethod.POST, consumes = "application/json")
    @Operation(summary = "Registra um novo incidente operacional")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Incidente criado com sucesso",
                    content = @Content(schema = @Schema(implementation = CreatedIncidentEnvelope.class))),
            @APIResponse(responseCode = "400", description = "Payload inválido")
    })
    public Uni<CreatedIncidentEnvelope> create(HttpServerResponse response, @Body @Valid CreateIncidentRequest request) {
        response.setStatusCode(201);
        return service.create(request)
                .onItem().transform(incident -> new CreatedIncidentEnvelope("Incident registered and queued for reactive triage.", incident));
    }

    @Route(path = "priority-board", methods = Route.HttpMethod.GET, order = 1)
    @Operation(summary = "Lista a fila priorizada de incidentes abertos")
    @APIResponse(responseCode = "200", description = "Fila priorizada",
            content = @Content(schema = @Schema(implementation = PriorityBoardResponse.class)))
    public Uni<PriorityBoardResponse> priorityBoard() {
        return service.priorityBoard();
    }

    @Route(path = "summary", methods = Route.HttpMethod.GET, order = 1)
    @Operation(summary = "Retorna o painel resumido de severidade e incidentes críticos")
    @APIResponse(responseCode = "200", description = "Resumo operacional",
            content = @Content(schema = @Schema(implementation = IncidentSummaryResponse.class)))
    public Uni<IncidentSummaryResponse> summary() {
        return service.summary();
    }

    @Route(path = "live", methods = Route.HttpMethod.GET, produces = "text/event-stream", order = 1)
    @Operation(summary = "Expõe um stream simples de snapshots para triagem")
    @APIResponse(responseCode = "200", description = "Stream SSE com snapshots do despacho reativo")
    public Multi<String> live() {
        return service.liveSignals().onItem().transform(this::toSsePayload);
    }

    @Route(path = ":incidentId", methods = Route.HttpMethod.GET, order = 2)
    @Operation(summary = "Retorna um incidente específico pelo identificador")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Incidente encontrado",
                    content = @Content(schema = @Schema(implementation = IncidentResponse.class))),
            @APIResponse(responseCode = "404", description = "Incidente não encontrado")
    })
    public Uni<IncidentResponse> incidentById(RoutingContext context) {
        return service.findById(context.pathParam("incidentId"));
    }

    private String toSsePayload(IncidentLiveSignal signal) {
        try {
            return "event: incident-snapshot\ndata: " + objectMapper.writeValueAsString(signal) + "\n\n";
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize live signal", exception);
        }
    }
}
