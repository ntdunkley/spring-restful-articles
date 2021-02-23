package com.nathan.springrestfularticles.dao.repositories;

import com.nathan.springrestfularticles.dao.entities.Article;
import org.springframework.data.repository.CrudRepository;

public interface ArticleRepository extends CrudRepository<Article, Long> {
}
