package co.miniso.rompefilas.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "clientEntityManagerFactory",
        basePackages = "co.miniso.rompefilas.db3.repository",
        transactionManagerRef = "clientTransactionManager")
public class DB3DataSourceConfig {

    @Value("${spring.datasource.db3.jdbc-url}")
    private String jdbcUrl;

    @Value("${spring.datasource.db3.username}")
    private String username;

    @Value("${spring.datasource.db3.password}")
    private String password;

    @Value("${spring.datasource.db3.driver-class-name}")
    private String driverClassName;

    @Bean(name = "clientDataSource")
    public DataSource dataSource() {
        HikariConfig config = getHikariConfig();

        // Configuración importante para que Hibernate pueda obtener información
        config.addDataSourceProperty("applicationName", "RompeFilas-Client");

        // Propiedades específicas para SQL Server
        config.addDataSourceProperty("trustServerCertificate", "true");
        config.addDataSourceProperty("encrypt", "false");

        HikariDataSource dataSource = new HikariDataSource(config);
        return dataSource;
    }

    private HikariConfig getHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);

        // Configuración específica de HikariCP
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(600000);
        config.setAutoCommit(true);
        config.setConnectionTimeout(30000);
        config.setPoolName("HikariPool-Client");
        return config;
    }

    @Bean(name = "db3JpaProperties")
    @ConfigurationProperties("spring.jpa.db3")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Bean(name = "db3EntityManagerFactoryBuilder")
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(
            @Qualifier("db3JpaProperties") JpaProperties jpaProperties) {
        return new EntityManagerFactoryBuilder(
                new org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter(),
                jpaProperties.getProperties(), null);
    }

    @Bean(name = "clientEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean clientEntityManagerFactory(
            @Qualifier("db3EntityManagerFactoryBuilder") EntityManagerFactoryBuilder builder,
            @Qualifier("clientDataSource") DataSource dataSource) {

        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");

        return builder
                .dataSource(dataSource)
                .packages("co.miniso.rompefilas.db3.model")
                .properties(properties)
                .persistenceUnit("db3")
                .build();
    }

    @Bean(name = "clientTransactionManager")
    public PlatformTransactionManager clientTransactionManager(
            @Qualifier("clientEntityManagerFactory") EntityManagerFactory clientEntityManagerFactory) {
        return new JpaTransactionManager(clientEntityManagerFactory);
    }
}
