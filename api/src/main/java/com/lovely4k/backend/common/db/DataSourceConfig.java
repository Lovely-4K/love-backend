package com.lovely4k.backend.common.db;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Profile({"prod1", "prod2"})
@Configuration
public class DataSourceConfig {

    //등록해야 하는 DataSource 클래스의 bean이 너무 많아서 각기 다른 bean 이름을 지정하고 재사용
    private static final String MASTER_DATASOURCE = "masterDataSource";
    private static final String SLAVE_DATASOURCE = "slaveDataSource";
    private static final String ROUTING_DATASOURCE = "routingDataSouce";

    @Bean(MASTER_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.master.hikari")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .build();
    }


    // slave database DataSource

    @Bean(SLAVE_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.slave.hikari")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .build();
    }

    // routing dataSource Bean
    @Bean(ROUTING_DATASOURCE)
    @DependsOn({MASTER_DATASOURCE, SLAVE_DATASOURCE})
    public DataSource routingDataSource(
        @Qualifier(MASTER_DATASOURCE) DataSource masterDataSource,
        @Qualifier(SLAVE_DATASOURCE) DataSource slaveDataSource) {

        RoutingDataSource routingDatasource = new RoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DatabaseType.MASTER, masterDataSource);
        dataSourceMap.put(DatabaseType.SLAVE, slaveDataSource);

        // dataSource Map 설정
        routingDatasource.setTargetDataSources(dataSourceMap);
        // default DataSource는 master로 설정
        routingDatasource.setDefaultTargetDataSource(masterDataSource);

        return routingDatasource;
    }

    /** @Transactional 어노테이션이 붙으면 아래의 과정으로 트랜잭션이 실행됨
     * 1. CglibAopProxy.DynamicAdvisedInterceptor.intercept( )
     * 2. TransactionInterceptor.invoke( )
     * 3. TransactionAspectSupport.invokeWithinTransaction( )
     * 4. TransactionAspectSupport.createTransactionIfNecessary( )
     * 5. AbstractPlatformTransactionManager.getTransaction( )
     * 6. AbstractPlatformTransactionManager.startTransaction( )
     * 7. AbstractPlatformTransactionManager.begin( )
     * 8. AbstractPlatformTransactionManager.prepareSynchronization( )
     * 9. TransactionAspectSupport.invokeWithinTransaction( )
     * 10. InvocationCallback.proceedWithInvocation( )
     * 11. @Transactional이 적용된 실제 타깃이 실행
     *
     * 위에 등록한 bean 만으로는 내가 의도한대로 db 커넥션을 얻지 않음.(그냥 랜덤으로 db 커넥션을 @Transactional 시작과 동시에 얻음) 하지만, 이렇게 LazyConnection을 열어줌으로써 db에 접근하는 순간 커넥션을 얻게 함. 이 때 얻는 커넥션은 내가 설정한 routingDataSource에서 의도한대로 커넥션을 얻을 수 있도록 설정했음.
     * 참고로 master-slave 구성을 하지 않더라도 이런 식으로 커넥션을 얻는 시간을 늦춰서 약간의 최적화가 가능하기도 함.
     * 한줄요약: 트랜잭션 메소드가 호출 될 때 커넥션을 얻냐 db에 접근하는 순간 커넥션을 얻냐 차이
     */
    @Bean
    @Primary
    @DependsOn(ROUTING_DATASOURCE)
    public DataSource dataSource(@Qualifier(ROUTING_DATASOURCE) DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

}