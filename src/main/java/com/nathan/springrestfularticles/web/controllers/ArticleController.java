package com.nathan.springrestfularticles.web.controllers;

import com.nathan.springrestfularticles.dao.entities.Article;
import com.nathan.springrestfularticles.dao.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @PostMapping("/create")
    public String create(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "author") String author,
            @RequestParam(value = "content") String content
    ) {
        Article article = articleRepository.save(new Article(title, author, content));
        return "Created Article " + article + "\n";
    }

    @GetMapping("/get")
    public String get(@RequestParam("id") long id) {
        Optional<Article> article = articleRepository.findById(id);
        return "Found article " + article.get() + " \n";
    }

    @GetMapping("/getAll")
    public String getAll() {
        Iterable<Article> articles = articleRepository.findAll();
        StringBuilder articleList = new StringBuilder();
        int count = 1;
        for (Article article : articles) {
            articleList.append(count++).append("\t->\t");
            articleList.append(article).append("\n");
        }
        return articleList.toString();
    }
}
