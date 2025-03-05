package co.miniso.rompefilas.db2.repository;

import co.miniso.rompefilas.db2.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, String> {

    @Query(value = """
        USE EMISION_FE;
        SELECT 
            oi.NumAtCard AS NumAtCard,
            oi.HoraEmisionPos AS HoraEmisionPos,
            oi.DocTotal AS DocTotal,
            oi.CC_Nit AS CC_Nit,
            oi.Store,
            oi.VendedorPOS,
            oi.Nombre_Cliente AS Nombre_Cliente,
            i.Sku AS Sku,
            i.codigobarras AS codigobarras,
            i.Articulo AS Articulo,
            i.Cantidad AS Cantidad,
            i.PrecioSinImpuesto AS PrecioSinImpuesto
        FROM OINV_FE oi
        INNER JOIN INV_FE i ON oi.Store = i.Store AND oi.DocNum = i.DocNum
        WHERE oi.NumAtCard = :numAtCard
        """,
            nativeQuery = true)
    List<Object[]> findByNumAtCard(@Param("numAtCard") String numAtCard);
}
