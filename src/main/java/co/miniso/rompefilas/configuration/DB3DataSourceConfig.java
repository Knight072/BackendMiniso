package co.miniso.rompefilas.configuration;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "clientEntityManagerFactory",
        basePackages = "co.miniso.rompefilas.db3.repository",
        transactionManagerRef = "clientTransactionManager")
public class DB3DataSourceConfig {

    @Bean(name = "clientDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db3")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "clientEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean clientEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                           @Qualifier("clientDataSource") DataSource dataSource, JpaProperties jpaProperties) {
        return builder.dataSource(dataSource)
                // Ubica las entidades para la base de datos APP
                .packages("co.miniso.rompefilas.db3.model")
                .build();
    }

    @Primary
    @Bean(name = "clientTransactionManager")
    public PlatformTransactionManager clientTransactionManager(
            @Qualifier("clientEntityManagerFactory") EntityManagerFactory clientEntityManagerFactory) {
        return new JpaTransactionManager(clientEntityManagerFactory);
    }
}
