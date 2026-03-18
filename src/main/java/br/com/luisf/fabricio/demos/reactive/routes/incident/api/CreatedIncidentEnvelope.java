package br.com.luisf.fabricio.demos.reactive.routes.incident.api;

public record CreatedIncidentEnvelope(
        String message,
        IncidentResponse incident) {
}
