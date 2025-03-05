package co.miniso.rompefilas.service;

import co.miniso.rompefilas.db2.model.Article;
import co.miniso.rompefilas.db2.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    private ArticleRepository articleRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository) {

        this.articleRepository = articleRepository;
    }

    public List<Article> getByItemPosNo(String itemPosNo){
        return articleRepository.obtenerProductosPorItemPosNo(itemPosNo);
    }
}
