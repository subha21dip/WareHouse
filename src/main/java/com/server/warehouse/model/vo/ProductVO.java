package com.server.warehouse.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductVO implements  Comparable<ProductVO>{
    private String name;
    private double price;
    private int priority;
    @JsonProperty("contain_articles")
    List<ProductArticlesVO> articles;

    public List<ProductArticlesVO> getArticles() {
        return articles;
    }

    public void setArticles(List<ProductArticlesVO> articles) {
        this.articles = articles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(ProductVO o) {
        return this.getPriority() - o.getPriority();
    }
}
