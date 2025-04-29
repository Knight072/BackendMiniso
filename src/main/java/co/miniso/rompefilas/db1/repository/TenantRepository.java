package co.miniso.rompefilas.db1.repository;

import co.miniso.rompefilas.db1.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
    @Query("SELECT t.store, t.name FROM Tenant t")
    List<Object[]> getNameStores();

    @Modifying
    @Query("UPDATE Tenant t SET t.consecutive = t.consecutive + 1 WHERE t.store = :storeId")
    void increaseConsecutive(@Param("storeId") Integer storeId);

    @Query("SELECT t.consecutive FROM Tenant t WHERE t.store = :storeId")
    int getConsecutive(@Param("storeId") int storeId);
}

