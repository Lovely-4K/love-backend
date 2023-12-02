package com.lovely4k.backend.common.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        log.info("isCurrentTransactionReadOnly={}", TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            return LoadBalancer.weightRoundRobin();
        }

        return DatabaseType.MASTER;
    }
}