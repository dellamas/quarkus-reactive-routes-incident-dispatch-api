package br.com.luisf.fabricio.demos.reactive.routes.incident.domain;

import java.time.OffsetDateTime;

public record Incident(
        String id,
        Severity severity,
        String affectedService,
        String summary,
        String owner,
        IncidentStatus status,
        OffsetDateTime openedAt,
        OffsetDateTime updatedAt) {
}
