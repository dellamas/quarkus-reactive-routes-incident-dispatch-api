# quarkus-reactive-routes-incident-dispatch-api

Este projeto mostra uma API de triagem operacional com Quarkus Reactive Routes, fila priorizada, resumo executivo e stream SSE para acompanhamento contínuo de incidentes.

A estrutura foi mantida simples para destacar a extensão e facilitar a leitura do fluxo principal da aplicação.

## Stack

- Java 21
- Maven
- Quarkus
- Reactive Routes
- Jackson
- SmallRye OpenAPI
- Swagger UI
- SmallRye Health
- Bean Validation

## Estrutura

- `api/` concentra contratos e rotas HTTP
- `domain/` define severidade, status e entidade do incidente
- `service/` concentra a regra de triagem, ordenação e emissão do stream
- `health/` expõe o health check da aplicação

## Endpoints

- `POST /incidents`
- `GET /incidents/priority-board`
- `GET /incidents/summary`
- `GET /incidents/live`
- `GET /q/openapi`
- `GET /q/swagger-ui`
- `GET /q/health`

## Como rodar

```bash
mvn quarkus:dev
```

## Como testar

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

## Documentação oficial

https://quarkus.io/guides/reactive-routes

## LinkedIn

https://www.linkedin.com/in/luisfabriciodellamas/
