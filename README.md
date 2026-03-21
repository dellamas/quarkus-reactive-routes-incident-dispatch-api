# quarkus-reactive-routes-incident-dispatch-api

Se você quiser ver `quarkus-reactive-routes` fora do exemplo genérico, este projeto é um bom ponto de partida. A aplicação simula uma API de triagem operacional com fila priorizada, resumo executivo e stream SSE para acompanhamento contínuo de incidentes.

A base técnica aqui é simples de propósito. O foco está em mostrar a extensão funcionando num cenário que parece serviço de verdade, sem encher o projeto de infraestrutura que só atrapalha a leitura.

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

O fluxo principal está dividido assim:

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

## Artigo relacionado

https://dev.to/dellamas/triagem-reativa-de-incidentes-operacionais-com-quarkus-reactive-routes-i6l

## LinkedIn

Se quiser acompanhar mais conteúdo meu sobre Java, Quarkus, arquitetura e comunidade, me segue no LinkedIn:

https://www.linkedin.com/in/luisfabriciodellamas/
