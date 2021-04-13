package com.server.warehouse.model.entity;

import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "quantity")
    private long count;

    public Article() {

    }

    public Article(Long id, String name, long count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}

