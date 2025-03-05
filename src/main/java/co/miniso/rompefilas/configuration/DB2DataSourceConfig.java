package co.miniso.rompefilas.configuration;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
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

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "articleEntityManagerFactory",
		basePackages = "co.miniso.rompefilas.db2.repository",
		transactionManagerRef = "articleTransactionManager")
public class DB2DataSourceConfig {

	@Bean(name = "articleDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.db2")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "articleEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean articleEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("articleDataSource") DataSource dataSource,JpaProperties jpaProperties) {
		return builder.dataSource(dataSource)
				// Ubica las entidades para la base de datos APP
				.packages("co.miniso.rompefilas.db2.model")
				.build();
	}

	@Primary
	@Bean(name = "articleTransactionManager")
	public PlatformTransactionManager articleTransactionManager(
			@Qualifier("articleEntityManagerFactory") EntityManagerFactory articleEntityManagerFactory) {
		return new JpaTransactionManager(articleEntityManagerFactory);
	}
}
