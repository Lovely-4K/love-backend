package com.lovely4k.backend.common.log;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;

@Slf4j
public class PrepareStatementInspector implements StatementInspector {

    @Override
    public String inspect(String sql) {
        log.info("sql={}", sql);
        return sql;
    }

}