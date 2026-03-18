package br.com.luisf.fabricio.demos.reactive.routes.incident.api;

import java.time.OffsetDateTime;

public record IncidentLiveSignal(
        int sequence,
        String channel,
        OffsetDateTime generatedAt,
        IncidentSummaryResponse summary,
        IncidentResponse nextPriorityIncident) {
}
