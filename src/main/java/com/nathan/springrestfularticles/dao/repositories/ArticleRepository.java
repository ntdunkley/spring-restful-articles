package com.nathan.springrestfularticles.dao.repositories;

import com.nathan.springrestfularticles.dao.entities.Article;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface ArticleRepository extends CrudRepository<Article, Long> {
    Iterable<Article> findByAuthor(String author);
    Iterable<Article> findByDateGreaterThanEqual(LocalDate oneWeekAgo);
}
