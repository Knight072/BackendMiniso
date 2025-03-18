package co.miniso.rompefilas.service;

import co.miniso.rompefilas.db1.model.Tenant;
import co.miniso.rompefilas.db1.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    // Inyectar valores desde application.properties
    @Value("${spring.datasource.db2.username}")
    private String dbUsername;

    @Value("${spring.datasource.db2.password}")
    private String dbPassword;

    @Autowired
    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    /**
     * Obtiene la URL de conexión de un tenant.
     */
    public String getDatabaseUrl(String tenant) {
        Tenant t = tenantRepository.findById(tenant).orElse(null);
        if (t == null) return null;

        return String.format(
                "jdbc:sqlserver://%s:1433;databaseName=master;user=%s;password=%s;encrypt=false;trustServerCertificate=true",
                t.getIpAddress(), dbUsername, dbPassword
        );
    }

    /**
     * Obtiene todos los tenants y sus bases de datos.
     */
    public Map<String, String> getAllTenants() {
        return tenantRepository.findAll().stream()
                .collect(Collectors.toMap(Tenant::getName, t ->
                        String.format(
                                "jdbc:sqlserver://%s:1433;databaseName=master;user=%s;password=%s;encrypt=false;trustServerCertificate=true",
                                t.getIpAddress(), dbUsername, dbPassword
                        )));
    }

    public List<Object[]> getNameStores() {
        return tenantRepository.getNameStores();
    }
}
