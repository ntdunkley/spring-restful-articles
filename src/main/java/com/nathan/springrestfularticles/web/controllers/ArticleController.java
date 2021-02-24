package com.nathan.springrestfularticles.web.controllers;

import com.nathan.springrestfularticles.dao.entities.Article;
import com.nathan.springrestfularticles.dao.repositories.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/article")
@Validated
public class ArticleController {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleController.class);
    @Autowired
    private ArticleRepository articleRepository;

    @PostMapping("/create")
    public String create(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("content") String content,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (title.length() > 100) {
            return "Title cannot exceed 100 characters";
        }
        Article article = articleRepository.save(new Article(title, author, content, date));
        return "Created Article " + article + "\n";
    }

    @GetMapping("/getById")
    public String getById(@RequestParam("id") long id) {
        Optional<Article> article = articleRepository.findById(id);
        return article.get() + " \n";
    }

    @GetMapping("/getAll")
    public String getAll() {
        Iterable<Article> articles = articleRepository.findAll();
        return buildArticleReport(articles);
    }

    @GetMapping("/getByAuthor")
    public String getByAuthor(@RequestParam("author") String author) {
        Iterable<Article> articles = articleRepository.findByAuthor(author);
        return buildArticleReport(articles);
    }

    @GetMapping("/statistics")
    public String statistics() {
        LocalDate currentDate = LocalDate.now();
        LOG.info(currentDate.toString());
        LOG.info(currentDate.minus(1, ChronoUnit.WEEKS).toString());
        // TODO: This assumes published dates aren't set in the future. Fix.
        Iterable<Article> articles = articleRepository.findByDateGreaterThanEqual(currentDate.minus(1, ChronoUnit.WEEKS));
        return ((Collection<?>) articles).size() + "\n";
    }

    private String buildArticleReport(Iterable<Article> articles) {
        StringBuilder articleList = new StringBuilder();
        int count = 1;
        for (Article article : articles) {
            articleList.append(count++).append("\t->\t");
            articleList.append(article).append("\n");
        }
        return articleList.toString();
    }
}
