package com.dungi.storage.rdb.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@EnableTransactionManagement
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@Configuration
public class DatabaseConfig {
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.source")
    public DataSourceProperties sourceDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.source.hikari")
    public HikariDataSource sourceDataSource(
            @Qualifier("sourceDataSourceProperties") DataSourceProperties properties
    ) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @ConditionalOnProperty(name = "database.replication", havingValue = "true")
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.replica")
    public DataSourceProperties replicaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @ConditionalOnProperty(name = "database.replication", havingValue = "true")
    @Bean
    @ConfigurationProperties("spring.datasource.replica.hikari")
    public HikariDataSource replicaDataSource(
            @Qualifier("replicaDataSourceProperties") DataSourceProperties properties
    ) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.source")
    public DataSource sourceDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @ConditionalOnProperty(name = "database.replication", havingValue = "true")
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.replica")
    public DataSource replicaDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @ConditionalOnProperty(name = "database.replication", havingValue = "true")
    @Bean
    public DataSource routingDataSource(
            @Qualifier("sourceDataSource") DataSource masterDataSource,
            @Qualifier("replicaDataSource") DataSource slaveDataSource) {
        var routingDataSource = new ReplicationRoutingDataSource();

        var dataSourceMap = new HashMap<>();
        dataSourceMap.put("source", masterDataSource);
        dataSourceMap.put("replica", slaveDataSource);
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);

        return routingDataSource;
    }

    @ConditionalOnProperty(name = "database.replication", havingValue = "true")
    @Primary
    @Bean
    public DataSource dataSource(
            @Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}
