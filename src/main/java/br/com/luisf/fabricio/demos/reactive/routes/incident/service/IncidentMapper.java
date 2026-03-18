package br.com.luisf.fabricio.demos.reactive.routes.incident.service;

import br.com.luisf.fabricio.demos.reactive.routes.incident.api.IncidentResponse;
import br.com.luisf.fabricio.demos.reactive.routes.incident.domain.Incident;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IncidentMapper {

    public IncidentResponse toResponse(Incident incident) {
        return new IncidentResponse(
                incident.id(),
                incident.severity(),
                incident.affectedService(),
                incident.summary(),
                incident.owner(),
                incident.status(),
                incident.openedAt(),
                incident.updatedAt());
    }
}
