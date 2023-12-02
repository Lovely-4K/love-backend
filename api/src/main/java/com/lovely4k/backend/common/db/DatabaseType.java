package com.lovely4k.backend.common.db;

public enum DatabaseType {
    MASTER(2),
    SLAVE(3);

    private final int weight;

    DatabaseType(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }
}