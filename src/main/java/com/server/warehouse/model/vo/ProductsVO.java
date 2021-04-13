package com.server.warehouse.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductsVO {

    List<ProductVO> products;

    public List<ProductVO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductVO> products) {
        this.products = products;
    }
}
