package com.nathan.springrestfularticles.web.controllers;

import com.nathan.springrestfularticles.dao.entities.Article;
import com.nathan.springrestfularticles.dao.repositories.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/article")
public class ArticleController {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleController.class);

    private static final int PAGE_SIZE = 2;

    @Autowired
    private ArticleRepository articleRepository;

    @PostMapping(value = "/create", consumes = "application/json")
    public String create(@RequestBody Article article) {
        LOG.debug("Title length: " + article.getTitle().length());
        if (article.getTitle() == null || article.getTitle().isEmpty()) {
            return "Title must be provided\n";
        }
        if (article.getAuthor() == null || article.getAuthor().isEmpty()) {
            return "Author must be provided\n";
        }
        if (article.getContent() == null || article.getContent().isEmpty()) {
            return "Content must be provided\n";
        }
        if (article.getDate() == null) {
            return "Date must be provided\n";
        }
        if (article.getTitle().length() > 100) {
            return "Title cannot exceed 100 characters\n";
        }
        Article savedArticle = articleRepository.save(article);
        return "Created Article: " + savedArticle + "\n";
    }

    @GetMapping("/getById")
    public String getById(@RequestParam("id") long id) {
        Optional<Article> article = articleRepository.findById(id);
        return article.get() + " \n";
    }

    @GetMapping("/getAll")
    public String getAll() {
        StringBuilder articleReport = new StringBuilder();
        Pageable currentPage = PageRequest.of(0, PAGE_SIZE);
        Page<Article> articles = articleRepository.findAll(currentPage);
        while (!articles.isEmpty()) {
            articleReport.append(buildArticlePage(articles, currentPage.getPageNumber()));
            currentPage = currentPage.next();
            articles = articleRepository.findAll(currentPage);
        }
        return articleReport.toString();
    }

    @GetMapping("/getByAuthor")
    public String getByAuthor(@RequestParam("author") String author) {
        Iterable<Article> articles = articleRepository.findByAuthor(author);
        return buildArticleReport(articles);
    }

    @GetMapping("/statistics")
    public String statistics() {
        LocalDate currentDate = LocalDate.now();
        LocalDate oneWeekAgo = currentDate.minus(1, ChronoUnit.WEEKS);
        Iterable<Article> articles = articleRepository.findByDateIsBetween(oneWeekAgo, currentDate);
        return ((Collection<Article>) articles).size() + "\n";
    }

    private String buildArticleReport(Iterable<Article> articles) {
        StringBuilder articleList = new StringBuilder();
        for (Article article : articles) {
            articleList.append(article).append("\n");
        }
        return articleList.toString();
    }

    private String buildArticlePage(Page<Article> articles, int pageNumber) {
        StringBuilder articleReport = new StringBuilder();
        articleReport.append("Page " + pageNumber + ": ");
        // Loop through articles in current page
        articles.forEach((article) -> articleReport.append(article).append(", "));
        articleReport.append("\n");
        return articleReport.toString();
    }
}
