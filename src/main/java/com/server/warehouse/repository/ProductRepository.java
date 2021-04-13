package com.server.warehouse.repository;

import com.server.warehouse.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    void deleteByName(String name);

    Optional<Product> findByName(String name);
}
