# quarkus-reactive-routes-incident-dispatch-api

API didática em Quarkus para despacho e triagem reativa de incidentes operacionais, com foco em observabilidade, fila priorizada, resumo executivo e stream contínuo de snapshots.

O projeto usa `quarkus-reactive-routes` sobre Vert.x/Netty, mantém dados em memória para demonstração rápida e expõe documentação OpenAPI/Swagger UI e health checks prontos para inspeção local. A referência principal de implementação é a documentação oficial de Reactive Routes da Quarkus: https://quarkus.io/guides/reactive-routes.

## Stack

- Java 21
- Maven
- Quarkus
- Reactive Routes
- Jackson
- SmallRye OpenAPI e Swagger UI
- SmallRye Health
- Bean Validation

## Endpoints

- `POST /incidents`
- `GET /incidents/priority-board`
- `GET /incidents/summary`
- `GET /incidents/live`
- `GET /q/openapi`
- `GET /q/swagger-ui`
- `GET /q/health`

## Executando

```bash
mvn quarkus:dev
```

Para rodar os testes:

```bash
mvn test
```

## Exemplo de criação

```bash
curl -X POST http://localhost:8080/incidents \
  -H "Content-Type: application/json" \
  -d '{
    "severity": "CRITICAL",
    "affectedService": "payments-api",
    "summary": "Erro sustentado no fluxo de autorização",
    "owner": "platform-sre"
  }'
```

## Observações

O endpoint `GET /incidents/live` entrega `text/event-stream` com snapshots curtos do estado operacional. Para navegar pelo código ou reaproveitar o material, o repositório publicado está em https://github.com/dellamas/quarkus-reactive-routes-incident-dispatch-api. Para contato profissional do autor, o LinkedIn está em https://br.linkedin.com/in/luisfabriciodellamas.
