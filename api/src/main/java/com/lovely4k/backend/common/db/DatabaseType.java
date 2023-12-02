package com.lovely4k.backend.common.db;

public enum DatabaseType {
    MASTER(1),
    SLAVE(10);

    private final int weight;

    DatabaseType(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }
}