# Triagem reativa de incidentes operacionais com Quarkus Reactive Routes

Quando uma operação começa a degradar, o problema nem sempre é falta de dado. Na prática, o que costuma faltar é velocidade para transformar sinais dispersos em decisão operacional. Erro 5xx subindo, fila acumulando, latência fora do SLO e um time tentando entender o que precisa entrar primeiro na esteira de resposta. Foi desse cenário real de observabilidade que nasceu esta API.

A proposta aqui é objetiva: construir uma API pequena, coerente e apresentável para despacho de incidentes operacionais usando `quarkus-reactive-routes`. Em vez de um exemplo genérico, a aplicação simula uma fila de triagem com severidade, serviço afetado, resumo, dono, painel resumido e um stream contínuo para acompanhar snapshots da operação.

## O que foi construído

O projeto expõe quatro rotas centrais:

- `POST /incidents` para registrar um novo incidente.
- `GET /incidents/priority-board` para listar a fila priorizada.
- `GET /incidents/summary` para obter totais por severidade e incidentes críticos abertos.
- `GET /incidents/live` para acompanhar um stream com snapshots da operação.

Além disso, a aplicação inclui OpenAPI, Swagger UI, health checks e validação básica de payload, mantendo a experiência de demonstração próxima de um serviço real, mesmo com armazenamento em memória.

## Por que Reactive Routes

O ponto forte de `quarkus-reactive-routes` é oferecer um modelo direto de rotas sobre Vert.x/Netty, sem esconder a natureza não bloqueante da aplicação. Esse estilo encaixa muito bem em APIs enxutas, principalmente quando a intenção é mostrar fluxos simples que retornam `Uni` e `Multi` de forma explícita.

Neste projeto, a fila priorizada e o resumo são retornados com `Uni`, enquanto o endpoint de acompanhamento ao vivo usa `Multi` para emitir eventos sequenciais em `text/event-stream`. Isso ajuda a demonstrar o uso legítimo de reactive routes em um contexto de observabilidade, onde atualizações incrementais fazem sentido.

## Estrutura e decisões práticas

A modelagem foi mantida simples:

- um tipo para severidade com prioridade explícita;
- um status operacional mínimo para diferenciar incidentes abertos, triados e resolvidos;
- um serviço em memória para facilitar leitura e teste;
- respostas JSON específicas para criação, painel e resumo.

Essa escolha evita ruído de infraestrutura e mantém o foco no que interessa para quem está estudando o recurso: o desenho da rota, o contrato HTTP e o fluxo reativo.

O `POST /incidents` recebe um payload pequeno e validado:

```json
{
  "severity": "HIGH",
  "affectedService": "telemetry-gateway",
  "summary": "Atraso na entrega de métricas de disponibilidade",
  "owner": "observability-core"
}
```

Depois do registro, o incidente já entra na fila aberta com identificador próprio e timestamps em UTC. A rota de painel devolve a fila ordenada por severidade para deixar clara a ideia de despacho operacional. Já o resumo agrega o volume por severidade e destaca incidentes críticos ainda abertos, que normalmente concentram a atenção de uma central de operação.

## Stream para acompanhamento operacional

Em incidentes reais, consultar apenas endpoints síncronos nem sempre basta. Há momentos em que o time quer abrir um terminal ou dashboard leve e observar a situação se atualizando sem ficar repetindo chamadas manualmente. Foi por isso que o projeto inclui `GET /incidents/live`.

Esse endpoint entrega eventos SSE com snapshots curtos contendo:

- sequência do evento;
- timestamp de geração;
- resumo consolidado;
- próximo incidente mais prioritário da fila.

É uma forma simples de demonstrar streaming com reactive routes sem transformar a aplicação em algo artificial.

## Teste e fechamento

Os testes automatizados cobrem criação de incidente, consulta da fila priorizada e leitura do resumo operacional. Isso já garante uma base útil para evoluir o projeto com persistência, autenticação ou integração com métricas e tracing depois.

Se você quiser explorar a implementação, adaptar o fluxo para a sua operação ou usar a estrutura como ponto de partida para uma API reativa maior, vale visitar o repositório em https://github.com/dellamas/quarkus-reactive-routes-incident-dispatch-api e seguir a evolução do código. É um exemplo curto, mas construído para conversar com problemas que equipes realmente enfrentam quando precisam responder rápido a incidentes.
