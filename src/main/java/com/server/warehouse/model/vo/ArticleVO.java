package com.server.warehouse.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleVO {
    @JsonProperty("art_id")
    private long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("stock")
    private long quantity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArticleVO(){}

    public ArticleVO(long id, String name, long quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
