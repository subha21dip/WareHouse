package com.server.warehouse.repository;

import com.server.warehouse.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    void deleteByName(String name);
}
