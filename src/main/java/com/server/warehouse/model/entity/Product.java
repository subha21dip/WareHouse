package com.server.warehouse.model.entity;

import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "price")
    @NotNull
    private double price;

    @Column(name = "quantity")
    @NotNull
    private long count;

    @Column(name = "priority")
    @NotNull
    private long priority;

    public Product() {

    }

    public Product(String name, double price, long count, Integer priority) {
        this.name = name;
        this.price = price;
        this.count = count;
        this.priority = priority;
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

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }
}

