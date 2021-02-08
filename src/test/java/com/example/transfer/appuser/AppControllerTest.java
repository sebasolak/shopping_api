package com.example.transfer.appuser;

import com.example.transfer.product.ProductService;
import com.example.transfer.service.SecurityContextHolderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class AppControllerTest {

    private AppUserService appUserService;
    private SecurityContextHolderService securityContextHolderService;
    private ProductService productService;
    private AppController appController;

    @BeforeEach
    void setUp() {
        appUserService = Mockito.mock(AppUserService.class);
        securityContextHolderService = Mockito.mock(SecurityContextHolderService.class);
        productService = Mockito.mock(ProductService.class);
        appController = new AppController(appUserService, securityContextHolderService, productService);
    }


    @Test
    void shouldUpdateAppUserAccountBalance() {

        given(securityContextHolderService.getUserUsername()).willReturn("tom");

        appController.updateAppUserAccountBalance("William", "Black", 523);

        Mockito.verify(appUserService).updateFounds("William", "Black", 523, "tom");
    }

    @Test
    void shouldGetAvailableProducts() {
    }

    @Test
    void getProduct() {
    }

    @Test
    void listLocalStorage() {
    }

    @Test
    void addProductToLocalStorage() {
    }

    @Test
    void deleteProductFromLocalStorage() {
    }

    @Test
    void buyProductFromLocalStorage() {
    }
}