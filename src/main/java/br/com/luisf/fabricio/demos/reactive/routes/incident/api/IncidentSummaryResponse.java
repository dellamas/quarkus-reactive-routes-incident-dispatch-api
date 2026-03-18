package br.com.luisf.fabricio.demos.reactive.routes.incident.api;

import br.com.luisf.fabricio.demos.reactive.routes.incident.domain.Severity;
import java.util.List;
import java.util.Map;

public record IncidentSummaryResponse(
        int totalIncidents,
        int openIncidents,
        Map<Severity, Long> totalsBySeverity,
        List<IncidentResponse> criticalOpenIncidents) {
}
