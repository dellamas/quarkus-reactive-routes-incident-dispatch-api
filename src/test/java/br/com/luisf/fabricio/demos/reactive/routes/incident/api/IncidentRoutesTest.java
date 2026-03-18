package br.com.luisf.fabricio.demos.reactive.routes.incident.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class IncidentRoutesTest {

    @Test
    void shouldCreateIncident() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "severity": "HIGH",
                          "affectedService": "telemetry-gateway",
                          "summary": "Atraso na entrega de métricas de disponibilidade",
                          "owner": "observability-core"
                        }
                        """)
                .when()
                .post("/incidents")
                .then()
                .statusCode(201)
                .body("message", equalTo("Incident registered and queued for reactive triage."))
                .body("incident.id", matchesPattern("INC-\\d+"))
                .body("incident.severity", equalTo("HIGH"))
                .body("incident.affectedService", equalTo("telemetry-gateway"))
                .body("incident.status", equalTo("OPEN"))
                .body("incident.openedAt", notNullValue());
    }

    @Test
    void shouldReturnPriorityBoard() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/incidents/priority-board")
                .then()
                .statusCode(200)
                .body("totalOpenIncidents", greaterThanOrEqualTo(3))
                .body("queue", hasSize(greaterThanOrEqualTo(3)))
                .body("queue[0].severity", equalTo("CRITICAL"));
    }

    @Test
    void shouldReturnOperationalSummary() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/incidents/summary")
                .then()
                .statusCode(200)
                .body("totalIncidents", greaterThanOrEqualTo(4))
                .body("openIncidents", greaterThanOrEqualTo(3))
                .body("totalsBySeverity.CRITICAL", greaterThanOrEqualTo(2))
                .body("criticalOpenIncidents", hasSize(greaterThanOrEqualTo(2)));
    }
}
