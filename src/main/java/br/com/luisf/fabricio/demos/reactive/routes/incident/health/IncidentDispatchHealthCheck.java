package br.com.luisf.fabricio.demos.reactive.routes.incident.health;

import br.com.luisf.fabricio.demos.reactive.routes.incident.service.IncidentDispatchService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class IncidentDispatchHealthCheck implements HealthCheck {

    private final IncidentDispatchService service;

    public IncidentDispatchHealthCheck(IncidentDispatchService service) {
        this.service = service;
    }

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("incident-dispatch-memory-store")
                .up()
                .withData("seededIncidents", service.totalIncidents())
                .build();
    }
}
