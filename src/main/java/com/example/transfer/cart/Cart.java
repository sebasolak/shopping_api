package com.example.transfer.cart;

import com.example.transfer.appuser.AppUser;
import com.example.transfer.product.Product;

import javax.persistence.*;
import java.util.List;

@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    public List<Product> productList;
    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "app_user_id"
    )
    private AppUser appUser;

    public Cart(List<Product> productList, AppUser appUser) {
        this.productList = productList;
        this.appUser = appUser;
    }


    public Cart() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
}
