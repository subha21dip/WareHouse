package com.server.warehouse.model.entity;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "product_details")
@IdClass(ProductDetailsId.class)
public class ProductDetails implements Serializable {

    @Id
    @Column(name = "product_name")
    private String productName;

    @Id
    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "article_quantity")
    @NotNull
    private long quantity;

    public ProductDetails(String productName, Long articleId, Long quantity) {
        this.productName = productName;
        this.articleId = articleId;
        this.quantity = quantity;
    }

    public ProductDetails(){}

    public String getProductId() {
        return productName;
    }

    public void setProductId(Long productId) {
        this.productName = productName;
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
