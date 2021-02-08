package com.example.transfer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlAddressProviderService {

    @Value("${product.list.url}")
    private String productListUrl;

    public String getProductListUrl() {
        return productListUrl;
    }
}
