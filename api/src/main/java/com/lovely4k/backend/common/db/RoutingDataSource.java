package com.lovely4k.backend.common.db;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {

        if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            return LoadBalancer.weightRoundRobin();
        }

        return DatabaseType.MASTER;
    }
}