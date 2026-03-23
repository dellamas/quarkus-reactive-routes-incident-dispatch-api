package br.com.luisf.fabricio.demos.reactive.routes.incident.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.luisf.fabricio.demos.reactive.routes.incident.api.CreateIncidentRequest;
import br.com.luisf.fabricio.demos.reactive.routes.incident.domain.Severity;
import org.junit.jupiter.api.Test;

class IncidentDispatchServiceTest {

    private final IncidentDispatchService service = new IncidentDispatchService(new IncidentMapper());

    @Test
    void shouldRejectBlankFieldsWhenServiceIsCalledDirectly() {
        CreateIncidentRequest request = new CreateIncidentRequest(Severity.HIGH, "payments-api", "   ", "platform");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.create(request).await().indefinitely());

        assertEquals("summary must not be blank", exception.getMessage());
    }
}
