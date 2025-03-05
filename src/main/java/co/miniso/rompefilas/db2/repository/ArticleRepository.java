package co.miniso.rompefilas.db2.repository;

import co.miniso.rompefilas.db2.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {

    @Query(value = "USE USICOAL;" +
            "SELECT pi.ITEM_POS_NO AS CodigoBarra, " +
            "       i.ITEM_ID AS Sku, " +
            "       p.DEPT_NAME AS NombreDept, " +
            "       i.ITEM_NAME AS Producto, " +
            "       i.PERM_UNIT_PRICE, " +
            "       i.CUR_UNIT_PRICE " +
            "FROM POS_IDENTITY pi " +
            "INNER JOIN ITEM i ON pi.ITEM_ID = i.ITEM_ID " +
            "INNER JOIN DEPARTMENT p ON i.DEPT_NO = p.DEPT_NO " +
            "WHERE pi.ITEM_POS_NO = :posNo",  // Filtro por parámetro
            nativeQuery = true)
    List<Article> obtenerProductosPorItemPosNo(@Param("posNo") String posNo);
}