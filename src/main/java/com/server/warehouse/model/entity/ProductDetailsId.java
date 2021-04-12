package com.server.warehouse.model.entity;

import java.io.Serializable;
import java.util.Objects;

public class ProductDetailsId implements Serializable {
    private Long productId;
    private Long articleId;

    public ProductDetailsId() {
    }

    public ProductDetailsId(Long productId, Long articleId) {
        this.productId = productId;
        this.articleId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDetailsId productDetailsId = (ProductDetailsId) o;
        return productId.equals(productDetailsId.productId) &&
                articleId.equals(productDetailsId.articleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, articleId);
    }
}
