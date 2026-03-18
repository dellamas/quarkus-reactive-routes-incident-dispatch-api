package br.com.luisf.fabricio.demos.reactive.routes.incident.domain;

public enum Severity {
    CRITICAL(4),
    HIGH(3),
    MEDIUM(2),
    LOW(1);

    private final int priority;

    Severity(int priority) {
        this.priority = priority;
    }

    public int priority() {
        return priority;
    }
}
