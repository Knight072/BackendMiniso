package co.miniso.rompefilas.db2.repository;

import co.miniso.rompefilas.db2.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {

    @Query(value = """
            USE USICOAL;
            SELECT T0.ITEM_POS_NO AS CodigoBarras,
            T1.PLU_CODE AS Sku,
            T2.DEPT_NAME AS NombreDept,
            T1.POS_DESC AS Articulo,
            T1.ITEM_UNIT_PRICE AS Precio,
            CASE
                WHEN T1.TAX_GROUP_ID IN (21,22,23,29,30,35,36,42,43,47) THEN CAST(ROUND(T1.ITEM_UNIT_PRICE* (0.19/1.19),2) AS decimal(10,2))
                WHEN T1.TAX_GROUP_ID IN (39,33,17,18,27) THEN (0)
                WHEN T1.TAX_GROUP_ID IN (45) THEN (0)
            END AS ValorImpuesto,
            CASE
                WHEN T1.TAX_GROUP_ID IN (21,22,23,29,30,35,36,42,43,47) THEN CAST(ROUND((T1.ITEM_UNIT_PRICE / 1.19),2 ) AS decimal(10,2))
                WHEN T1.TAX_GROUP_ID IN (39,33,17,18,27) THEN CAST(T1.ITEM_UNIT_PRICE AS decimal(10,2))
                WHEN T1.TAX_GROUP_ID IN (45) THEN  CAST(T1.ITEM_UNIT_PRICE AS decimal(10,2))
            END AS PrecioSinImpuesto,
            CASE
                WHEN T1.TAX_GROUP_ID IN (21,22,23,29,30,35,36,42,43,47) THEN 'IVA 19%'
                WHEN T1.TAX_GROUP_ID IN (39,33,17,18,27) THEN 'IVA EXCENTO'
                WHEN T1.TAX_GROUP_ID IN (45) THEN 'IVA EXCLUIDO'
            END AS Impuesto
          FROM POS_IDENTITY T0
            INNER JOIN PLU T1 ON T1.ITEM_ID = T0.ITEM_ID
            INNER JOIN DEPARTMENT T2 ON T2.DEPT_NO = T1.DEPT_NO
          WHERE T0.ITEM_POS_NO =:posNo AND T0.ITEM_ID NOT IN (67105,67106);
          """, nativeQuery = true)
    List<Article> obtenerProductosPorItemPosNo(@Param("posNo") String posNo);
}