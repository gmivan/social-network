package ru.otus.highload.socialbackend.config.data.replication;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.otus.highload.socialbackend.config.data.MasterDataSourceConfig;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
//@ConfigurationProperties("spring.datasource-slave")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactorySlave",
        transactionManagerRef = "transactionManagerSlave",
        basePackages = {"ru.otus.highload.socialbackend.repository.slave"}
)
public class SlaveDataSourceConfig {

    private final static String PERSISTENCE_UNIT_NAME = "slave";

    @Resource
    private JpaVendorAdapter jpaVendorAdapter;

    @Bean("dataSourceSlave")
    public DataSource dataSourceSlave(SlaveDataSourceProp slaveDataSourceProp) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(slaveDataSourceProp.getDriverClassName());
        dataSource.setJdbcUrl(slaveDataSourceProp.getJdbcUrl());
        dataSource.setUsername(slaveDataSourceProp.getUsername());
        dataSource.setPassword(slaveDataSourceProp.getPassword());
        dataSource.setMaximumPoolSize(slaveDataSourceProp.getMaximumPoolSize());
        dataSource.setMinimumIdle(slaveDataSourceProp.getMinimumIdle());
        dataSource.setIdleTimeout(slaveDataSourceProp.getIdleTimeout());
        dataSource.setPoolName(slaveDataSourceProp.getPoolName());
        dataSource.setAutoCommit(true);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySlave(
            @Qualifier("dataSourceSlave") final DataSource dataSourceSlave) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSourceSlave);
        factoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        factoryBean.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
        factoryBean.setPackagesToScan("ru.otus.highload.socialbackend.domain");
        factoryBean.setJpaProperties(MasterDataSourceConfig.hibernateProperties());
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManagerSlave(EntityManagerFactory entityManagerFactorySlave) {
        return new JpaTransactionManager(entityManagerFactorySlave);
    }
}
