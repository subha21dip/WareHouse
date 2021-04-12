package com.server.warehouse.repository;

import com.server.warehouse.model.entity.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Long> {
   List<ProductDetails> findAllBYProductId(long id);
}
