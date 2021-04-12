package com.server.warehouse.repository;

import com.server.warehouse.model.entity.Article;
import com.server.warehouse.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}
