package co.miniso.rompefilas.service;

import co.miniso.rompefilas.db1.model.Tenant;
import co.miniso.rompefilas.db1.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

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
                "jdbc:sqlserver://%s:1433;databaseName=master;user=sa;password=Password1!;encrypt=false;trustServerCertificate=true",
                t.getIpAddress()
        );
    }

    /**
     * Obtiene todos los tenants y sus bases de datos.
     */
    public Map<String, String> getAllTenants() {
        return tenantRepository.findAll().stream()
                .collect(Collectors.toMap(Tenant::getName, t ->
                        "jdbc:sqlserver://" + t.getIpAddress() +
                                ":1433;databaseName=master;user=sa;password=Password1!;encrypt=false;trustServerCertificate=true"));
    }
}
