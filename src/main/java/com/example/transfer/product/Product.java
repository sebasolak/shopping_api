package com.example.transfer.product;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.net.URL;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer autoId;
    private Integer id;
    private String title;
    private double price;
    @Transient
    private String description;
    @Transient
    private String category;
    @Transient
    private URL image;

    public Product(Integer id, String title, double price) {
        this.id = id;
        this.title = title;
        this.price = price;
    }

    public Product() {
    }

    public Integer getId() {
        return id;
    }

    public Integer getAutoId() {
        return autoId;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public URL getImage() {
        return image;
    }
}
