package co.miniso.rompefilas.controller;

import co.miniso.rompefilas.configuration.DynamicDataSource;
import co.miniso.rompefilas.db2.model.Article;
import co.miniso.rompefilas.service.ArticleService;
import co.miniso.rompefilas.service.TenantService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = {"localhost:8080, localhost:3000, 192.168.84.17:8080"}, allowCredentials = "true")
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
    public ResponseEntity<List<Article>> getArticleByItemPosNo(
            @PathVariable("itemPosNo") String itemPosNo, @RequestHeader("X-Tienda") String tienda,
            HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            boolean hasAuthToken = Arrays.stream(cookies)
                    .anyMatch(cookie -> "authToken".equals(cookie.getName()));

            if (hasAuthToken) {
                if (!tenantService.getAllTenants().containsKey(tienda)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(null);
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

        // Si no hay cookie o no es válida, devolver 401 Unauthorized
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

}
