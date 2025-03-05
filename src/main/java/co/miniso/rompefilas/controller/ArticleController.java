package co.miniso.rompefilas.controller;

import co.miniso.rompefilas.db2.model.Article;
import co.miniso.rompefilas.service.ArticleService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
@RestController
@RequestMapping("/consultaArticulo")
public class ArticleController {

    private ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService){
        this.articleService = articleService;
    }

    @GetMapping("{itemPosNo}")
    public ResponseEntity<?> getArticleByItemPosNo(@PathVariable("itemPosNo") String itemPosNo, HttpServletRequest request) {
        // Obtener cookies de la solicitud
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            boolean hasAuthToken = Arrays.stream(cookies)
                    .anyMatch(cookie -> "authToken".equals(cookie.getName()));

            if (hasAuthToken) {
                List<Article> articles = articleService.getByItemPosNo(itemPosNo);
                return ResponseEntity.ok(articles);
            }
        }

        // Si no hay cookie o no es válida, devolver 401 Unauthorized
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acceso denegado: Sesión expirada o inválida.");
    }
}
