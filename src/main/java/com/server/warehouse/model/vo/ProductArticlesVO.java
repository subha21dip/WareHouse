package com.server.warehouse.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductArticlesVO {

    @JsonProperty("art_id")
    private long id;
    @JsonProperty("amount_of")
    private long quantity;

    public ProductArticlesVO(long id, long quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public ProductArticlesVO() {}

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
