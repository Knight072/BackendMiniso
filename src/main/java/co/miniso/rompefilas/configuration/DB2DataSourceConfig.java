package co.miniso.rompefilas.configuration;

import jakarta.persistence.EntityManagerFactory;
import co.miniso.rompefilas.service.TenantService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableJpaRepositories(
		basePackages = "co.miniso.rompefilas.db2.repository",
		entityManagerFactoryRef = "db2EntityManagerFactory",
		transactionManagerRef = "db2TransactionManager")
public class DB2DataSourceConfig {

	private final Map<Object, Object> dataSourceMap = new ConcurrentHashMap<>();
	private final DynamicDataSource dynamicDataSource = new DynamicDataSource();

	@Value("${spring.datasource.db2.username}")
	private String dbUsername;

	@Value("${spring.datasource.db2.password}")
	private String dbPassword;

	@Value("${spring.datasource.db2.driver-class-name}")
	private String dbDriverClassName;

	@Bean(name = "db2JpaProperties")
	@ConfigurationProperties(prefix = "spring.jpa.db2")
	public org.springframework.boot.autoconfigure.orm.jpa.JpaProperties jpaProperties() {
		return new org.springframework.boot.autoconfigure.orm.jpa.JpaProperties();
	}

	@Bean(name = "db2EntityManagerFactoryBuilder")
	public EntityManagerFactoryBuilder entityManagerFactoryBuilder(
			@Qualifier("db2JpaProperties") org.springframework.boot.autoconfigure.orm.jpa.JpaProperties jpaProperties) {
		return new EntityManagerFactoryBuilder(
				new org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter(),
				jpaProperties.getProperties(), null);
	}

	@Bean(name = "tenantDataSourceInitializer")
	@DependsOn("db1EntityManagerFactory")
	public TenantDataSourceInitializer initializeDataSources(TenantService tenantService) {
		tenantService.getAllTenants().forEach(this::addTenantDataSource);
		dynamicDataSource.setTargetDataSources(dataSourceMap);

		if (!dataSourceMap.isEmpty()) {
			dynamicDataSource.setDefaultTargetDataSource(dataSourceMap.values().iterator().next());
		}

		dynamicDataSource.afterPropertiesSet();

		// Return a new bean that can be used to represent this initialization
		return new TenantDataSourceInitializer();
	}

	private void addTenantDataSource(String tenant, String dbUrl) {
		dataSourceMap.computeIfAbsent(tenant, key -> {
			System.out.println("🔄 Creating new DataSource for tenant: " + tenant);

			return DataSourceBuilder.create()
					.url(dbUrl)
					.username(dbUsername)
					.password(dbPassword)
					.driverClassName(dbDriverClassName)
					.type(com.zaxxer.hikari.HikariDataSource.class)
					.build();
		});
	}

	@Bean(name = "db2DataSource")
	@DependsOn("tenantDataSourceInitializer")
	public DataSource dataSource() {
		return dynamicDataSource;
	}

	@Bean(name = "db2EntityManagerFactory")
	@DependsOn("db2DataSource")
	public LocalContainerEntityManagerFactoryBean articleEntityManagerFactory(
			@Qualifier("db2EntityManagerFactoryBuilder") EntityManagerFactoryBuilder builder,
			@Qualifier("db2DataSource") DataSource dataSource) {

		Map<String, String> properties = new HashMap<>();
		properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");

		return builder
				.dataSource(dataSource)
				.packages("co.miniso.rompefilas.db2.model")
				.properties(properties)
				.persistenceUnit("db2")
				.build();
	}

	@Bean(name = "db2TransactionManager")
	public PlatformTransactionManager articleTransactionManager(
			@Qualifier("db2EntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	// Simple class to represent the initialization state
	public static class TenantDataSourceInitializer {
		// This is just a marker class
	}
}