package com.server.warehouse.model.entity;

import java.io.Serializable;
import java.util.Objects;

public class ProductDetailsId implements Serializable {
    private String productName;
    private Long articleId;

    public ProductDetailsId() {
    }

    public ProductDetailsId(String productName, Long articleId) {
        this.productName = productName;
        this.articleId = articleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDetailsId productDetailsId = (ProductDetailsId) o;
        return productName.equals(productDetailsId.productName) &&
                articleId.equals(productDetailsId.articleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, articleId);
    }
}
