package co.miniso.rompefilas.controller;

import co.miniso.rompefilas.configuration.DynamicDataSource;
import co.miniso.rompefilas.db2.model.Article;
import co.miniso.rompefilas.service.ArticleService;
import co.miniso.rompefilas.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
@RestController
@RequestMapping("/consultaArticulo")
public class ArticleController {

    private final ArticleService articleService;
    private final TenantService tenantService;

    @Autowired
    public ArticleController(ArticleService articleService, TenantService tenantService) {
        this.articleService = articleService;
        this.tenantService = tenantService;
    }

    @GetMapping("{itemPosNo}")
    public ResponseEntity<?> getArticleByItemPosNo(
            @PathVariable("itemPosNo") String itemPosNo,
            @RequestHeader("X-Tienda") String tienda) {

        if (!tenantService.getAllTenants().containsKey(tienda)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Tienda no registrada.");
        }

        // 🔹 Cambiar la base de datos activa
        DynamicDataSource.setCurrentTenant(tienda);

        try {
            List<Article> articles = articleService.getByItemPosNo(itemPosNo);
            return ResponseEntity.ok(articles);
        } finally {
            DynamicDataSource.clear(); // 🔹 Limpia la tienda después de la petición
        }
    }

}
