package com.server.warehouse.model.entity;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "product_details")
@IdClass(ProductDetailsId.class)
public class ProductDetails implements Serializable {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Id
    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "article_quantity")
    @NotNull
    private Long quantity;

    public ProductDetails(Long productId, Long articleId, Long quantity) {
        this.productId = productId;
        this.articleId = articleId;
        this.quantity = quantity;
    }

    public ProductDetails(){}

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
