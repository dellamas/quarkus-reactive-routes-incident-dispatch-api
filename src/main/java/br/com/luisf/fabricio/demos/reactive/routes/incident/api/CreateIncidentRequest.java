package br.com.luisf.fabricio.demos.reactive.routes.incident.api;

import br.com.luisf.fabricio.demos.reactive.routes.incident.domain.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateIncidentRequest(
        @NotNull Severity severity,
        @NotBlank @Size(max = 80) String affectedService,
        @NotBlank @Size(max = 180) String summary,
        @NotBlank @Size(max = 80) String owner) {
}
