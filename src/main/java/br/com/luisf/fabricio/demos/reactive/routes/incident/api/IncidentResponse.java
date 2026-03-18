package br.com.luisf.fabricio.demos.reactive.routes.incident.api;

import br.com.luisf.fabricio.demos.reactive.routes.incident.domain.IncidentStatus;
import br.com.luisf.fabricio.demos.reactive.routes.incident.domain.Severity;
import java.time.OffsetDateTime;

public record IncidentResponse(
        String id,
        Severity severity,
        String affectedService,
        String summary,
        String owner,
        IncidentStatus status,
        OffsetDateTime openedAt,
        OffsetDateTime updatedAt) {
}
