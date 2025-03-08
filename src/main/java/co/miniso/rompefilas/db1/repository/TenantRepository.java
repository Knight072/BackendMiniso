package co.miniso.rompefilas.db1.repository;

import co.miniso.rompefilas.db1.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
    @Query("SELECT t.name FROM Tenant t")
    List<String> getNameStores();
}

