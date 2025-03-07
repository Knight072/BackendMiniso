package co.miniso.rompefilas.configuration;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@EnableJpaRepositories(
		entityManagerFactoryRef = "db1EntityManagerFactory",
		basePackages = "co.miniso.rompefilas.db1.repository",
		transactionManagerRef = "db1TransactionManager"
)
public class DB1DataSourceConfig {

	@Primary
	@Bean(name = "db1Properties")
	@ConfigurationProperties("spring.datasource.db1")
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}

	@Primary
	@Bean(name = "db1DataSource")
	public DataSource dataSource(@Qualifier("db1Properties") DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().build();
	}

	@Primary
	@Bean(name = "db1JpaProperties")
	@ConfigurationProperties("spring.jpa.db1")
	public JpaProperties jpaProperties() {
		return new JpaProperties();
	}

	@Primary
	@Bean(name = "db1EntityManagerFactoryBuilder")
	public EntityManagerFactoryBuilder entityManagerFactoryBuilder(
			@Qualifier("db1JpaProperties") JpaProperties jpaProperties) {
		return new EntityManagerFactoryBuilder(
				new org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter(),
				jpaProperties.getProperties(), null);
	}

	@Primary
	@Bean(name = "db1EntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Qualifier("db1EntityManagerFactoryBuilder") EntityManagerFactoryBuilder builder,
			@Qualifier("db1DataSource") DataSource dataSource) {
		return builder
				.dataSource(dataSource)
				.packages("co.miniso.rompefilas.db1.model")
				.persistenceUnit("db1")
				.build();
	}

	@Primary
	@Bean(name = "db1TransactionManager")
	public PlatformTransactionManager transactionManager(
			@Qualifier("db1EntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
}