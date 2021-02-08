package com.example.transfer.appuser;

import com.example.transfer.msg.ServerMsg;
import com.example.transfer.product.Product;
import com.example.transfer.product.ProductDTO;
import com.example.transfer.product.ProductService;
import com.example.transfer.service.SecurityContextHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
@Validated
public class AppController {

    private final AppUserService appUserService;
    private final SecurityContextHolderService securityContextHolderService;
    private final ProductService productService;

    @Autowired
    public AppController(AppUserService appUserService,
                         SecurityContextHolderService securityContextHolderService,
                         ProductService productService) {
        this.appUserService = appUserService;
        this.securityContextHolderService = securityContextHolderService;
        this.productService = productService;
    }

    @PostMapping(path = "register")
    public void addNewAppUser(@RequestBody @Valid AppUser appUser) {
        appUserService.persistNewAppUser(appUser);
    }

    @GetMapping(path = "success")
    public String loginPage() {
        return "<div style=\"text-align:center\">" +
                "<h1 style=\"color:green\">Success Login Page</h1><div>";

    }

    @GetMapping(path = "transfer")
    public String updateAppUserAccountBalance(@RequestParam("name") String name,
                                              @RequestParam("sur") String surname,
                                              @RequestParam("transfer") int transfer) {
        appUserService.updateFounds(name, surname, transfer, securityContextHolderService.getUserUsername());
        return "updated";
    }

    @GetMapping(path = "prods")
    public Product[] getAvailableProducts(@QueryParam("max") Integer max) {
        System.out.println(max);
        return productService.selectAvailableProducts(Optional.ofNullable(max));
    }

    @GetMapping(path = "prods/{id}")
    public Product getProduct(@PathVariable("id") int productId) {
        return productService.selectProduct(productId);
    }

    @GetMapping(path = "local")
    public List<ProductDTO> listLocalStorage() {
        return appUserService.listLocalStorage(securityContextHolderService.getUserUsername());
    }

    @PostMapping(path = "add/{id}")
    public String addProductToLocalStorage(@PathVariable("id") int productId) {
        return appUserService.addProductToLocalStorage(securityContextHolderService.getUserUsername(), productId);
    }

    @DeleteMapping(path = "delete/{id}")
    public String deleteProductFromLocalStorage(@PathVariable("id") int productId) {
        return appUserService.deleteProductFromLocalStorage(securityContextHolderService.getUserUsername(), productId);
    }

    @PostMapping(path = "buy")
    public String buyProductFromLocalStorage() {
        return appUserService.buyProductFromLocalStorage(securityContextHolderService.getUserUsername());
    }

    @GetMapping(path = "price")
    public ServerMsg getTotalProductsPrice() {
        return appUserService.totalProductsPrice(securityContextHolderService.getUserUsername());
    }

    @PostMapping(path = "transfer/{updateSum}")
    public ServerMsg updateUserActualFounds(@PathVariable(value = "updateSum") int updateSum) {
        return appUserService.updateUserActualFounds(updateSum, securityContextHolderService.getUserUsername());
    }

    @GetMapping(path = "founds")
    public ServerMsg userFounds() {
        return appUserService.userFounds(securityContextHolderService.getUserUsername());
    }
}
