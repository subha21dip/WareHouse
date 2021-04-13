package com.server.warehouse.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryVO {
    @JsonProperty("inventory")
    List<ArticleVO> inventory;

    public List<ArticleVO> getInventory() {
        return inventory;
    }

    public void setInventory(List<ArticleVO> inventory) {
        this.inventory = inventory;
    }

}
