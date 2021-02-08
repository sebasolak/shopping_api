package com.example.transfer.product;

import com.example.transfer.appuser.AppUser;

import javax.persistence.ManyToOne;

public class ProductDTO {

    private Integer id;
    private String title;
    private double price;
    private AppUser appUser;

    public ProductDTO(Integer id, String title, double price, AppUser appUser) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.appUser = appUser;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                '}';
    }
}
