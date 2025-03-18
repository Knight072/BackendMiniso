package co.miniso.rompefilas.db1.repository;

import co.miniso.rompefilas.db1.model.BillApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillAppRepository extends JpaRepository<BillApp, String>{
}
