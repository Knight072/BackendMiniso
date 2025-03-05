package co.miniso.rompefilas.db3.repository;

import co.miniso.rompefilas.db3.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, String> {

    @Query(value = """
            SELECT TOP(1) 
                c.customer_no, 
                c.first_name as firstName, 
                c.last_name as lastName, 
                cak.alternate_key as identification,
                cak.alternate_key_code as typeIde, 
                c.gender as gender, 
                e.email_address as email, 
                e.modify_date as emailModifyDate
            FROM dbo.customer c
            LEFT JOIN dbo.customer_alternate_key cak ON c.customer_id = cak.customer_id
            LEFT JOIN dbo.address a ON c.customer_id = a.customer_id
            LEFT JOIN dbo.email e ON c.customer_id = e.customer_id
            WHERE cak.alternate_key = :customerId
            ORDER BY e.modify_date DESC
            """, nativeQuery = true)
    Optional<Client> findByDoc(@Param("customerId") String customerId);

    @Query(value = """
            SELECT CASE 
                WHEN COUNT(*) > 0 THEN 1 
                ELSE 0 
            END
            FROM dbo.email 
            WHERE email_address = :email
            """, nativeQuery = true)
    int validEmail(@Param("email") String email);
}
