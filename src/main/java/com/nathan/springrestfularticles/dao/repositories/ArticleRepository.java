package com.nathan.springrestfularticles.dao.repositories;

import com.nathan.springrestfularticles.dao.entities.Article;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;

public interface ArticleRepository extends PagingAndSortingRepository<Article, Long> {
    Iterable<Article> findByAuthor(String author);
    Iterable<Article> findByDateIsBetween(LocalDate today, LocalDate oneWeekAgo);
}
