package br.com.luisf.fabricio.demos.reactive.routes.incident.api;

import java.util.List;

public record PriorityBoardResponse(
        int totalOpenIncidents,
        List<IncidentResponse> queue) {
}
