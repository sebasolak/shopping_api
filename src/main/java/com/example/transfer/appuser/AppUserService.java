package com.example.transfer.appuser;

import com.example.transfer.bankaccount.BankAccount;
import com.example.transfer.bankaccount.BankAccountService;
import com.example.transfer.cart.Cart;
import com.example.transfer.cart.CartService;
import com.example.transfer.email.EmailSender;
import com.example.transfer.msg.ServerMsg;
import com.example.transfer.product.Product;
import com.example.transfer.product.ProductDTO;
import com.example.transfer.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "user with login %s not found";
    private final static String INSUFFICIENT_FUNDS_MSG = "Insufficient funds, your account balance was %s, while shopping cost was %s";
    public static List<ProductDTO> LOCAL_STORAGE_PRODS = new ArrayList<>();
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final BankAccountService bankAccountService;
    private final ProductService productService;
    private final CartService cartService;
    private final EmailSender emailSender;

    @Autowired
    public AppUserService(@Qualifier("appUserRepo") AppUserRepository appUserRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          BankAccountService bankAccountService,
                          ProductService productService,
                          CartService cartService,
                          EmailSender emailSender) {
        this.appUserRepository = appUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.bankAccountService = bankAccountService;
        this.productService = productService;
        this.cartService = cartService;
        this.emailSender = emailSender;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserActual(username);

    }

    public AppUser getUserActual(String username) {
        return appUserRepository.findByLogin(username)//login
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
    }

    @Transactional
    public void persistNewAppUser(AppUser appUser) {
        boolean userExists = appUserRepository.findByLogin(appUser.getLogin())
                .isPresent();
        if (userExists) {
            throw new IllegalStateException("login already taken");
        }

        String password = appUser.getPassword();
        if (password.length() < 3 || password.length() > 32) {
            throw new IllegalArgumentException("Password length must be between 3 and 32 characters");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        appUser.setPassword(encodedPassword);
        appUser.setAppUserRole(AppUserRole.USER);
        appUserRepository.save(appUser);

        BankAccount appUserBankAccount = new BankAccount(
                appUser.getAccountBalance(),
                appUser
        );
        bankAccountService.saveAppUserBankAccount(appUserBankAccount);
    }

    public AppUser selectAppUserByNameAndSurname(String name, String surname) {
        return appUserRepository.findAppUserByNameAndSurname(name, surname);
    }

    @Transactional
    public int updateFounds(String name, String surname, int transfer, String username) {
        AppUser appUserTo = selectAppUserByNameAndSurname(name, surname);
        AppUser userActual = getUserActual(username);

        //BankAccount accountBalance update
        bankAccountService.transferFounds(userActual, appUserTo, transfer);

        int accountBalanceAfterDebiting = userActual.getAccountBalance() - transfer;
        int accountBalanceAfterRecognition = appUserTo.getAccountBalance() + transfer;
        //AppUser accountBalance update
        userActual.setAccountBalance(accountBalanceAfterDebiting);
        appUserTo.setAccountBalance(accountBalanceAfterRecognition);
        return 1;
    }

    public List<ProductDTO> listLocalStorage(String username) {
        AppUser userActual = getUserActual(username);
        return LOCAL_STORAGE_PRODS.stream()
                .filter(productDTO -> productDTO.getAppUser().getLogin().equals(userActual.getLogin()))
                .collect(Collectors.toList());
    }

    @Transactional
    public String addProductToLocalStorage(String username, int productId) {
        if (productId < 1 || productId > 20) {
            throw new NoSuchElementException(String.format("product %d did not exists", productId));
        }
        AppUser userActual = getUserActual(username);
        Product product = productService.selectProduct(productId);
        ProductDTO productDTO = new ProductDTO(product.getId(), product.getTitle(), product.getPrice(), userActual);
        LOCAL_STORAGE_PRODS.add(productDTO);
        return String.format("%s added to local storage", productDTO.toString());
    }

    public String deleteProductFromLocalStorage(String username, int productId) {
        if (productId < 1 || productId > 20) {
            throw new NoSuchElementException(String.format("product %d did not exists", productId));
        }
        AppUser userActual = getUserActual(username);
        boolean productExists = false;
        for (ProductDTO localStorageProd : LOCAL_STORAGE_PRODS) {
            if (localStorageProd.getAppUser().getLogin().equals(userActual.getUsername()) &&
                    localStorageProd.getId() == productId) {
                LOCAL_STORAGE_PRODS.remove(localStorageProd);
                productExists = true;
                break;
            }
        }
        return productExists ? "Product was removed from local storage" : "Cannot remove product that did not exists";
    }

    @Transactional
    public String buyProductFromLocalStorage(String username) {
        AppUser userActual = getUserActual(username);

        List<Product> productList = LOCAL_STORAGE_PRODS.stream()
                .filter(productDTO -> productDTO.getAppUser().getLogin().equals(userActual.getUsername()))
                .map(productDTO -> new Product(
                        productDTO.getId(),
                        productDTO.getTitle(),
                        productDTO.getPrice()))
                .collect(Collectors.toList());

        if (productList.isEmpty()) {
            throw new IllegalStateException("Cannot buy nothing");
        }

        double totalSum = productList.stream().mapToDouble(Product::getPrice).sum();
        BankAccount bankAccount = bankAccountService.appUserBankAccount(userActual);

        if (totalSum > bankAccount.getAccountBalance()) {
            throw new IllegalStateException(String.format(INSUFFICIENT_FUNDS_MSG, bankAccount.getAccountBalance(), totalSum));
        }
        emailSender.send(userActual.getEmail(), productList.stream().map(
                product -> product.getPrice() + " " + product.getTitle())
                        .collect(Collectors.joining(" || ", "", ", and total cost was: " + totalSum)),
                "Thanks for choose our store"
        );

        int updatedFunds = (int) (bankAccount.getAccountBalance() - totalSum);

        bankAccount.setAccountBalance(updatedFunds);
        userActual.setAccountBalance(updatedFunds);

        LOCAL_STORAGE_PRODS = LOCAL_STORAGE_PRODS.stream()
                .filter(productDTO -> !productDTO.getAppUser().getLogin().equals(userActual.getUsername()))
                .collect(Collectors.toList());

        Cart cart = new Cart(productList, userActual);
        cartService.save(cart);
        return "Thank you for choose our store";
    }

    public ServerMsg totalProductsPrice(String username) {
        AppUser userActual = getUserActual(username);
        double totalSum = LOCAL_STORAGE_PRODS.stream()
                .filter(productDTO -> productDTO.getAppUser().getLogin().equals(userActual.getUsername()))
                .mapToDouble(ProductDTO::getPrice).sum();
        return new ServerMsg(String.format("Total product price is: %.2f", totalSum));
    }

    @Transactional
    public ServerMsg updateUserActualFounds(int updateSum, String username) {
        if (updateSum < 1) {
            throw new IllegalStateException("Update sum must be bigger than 0");
        }

        AppUser userActual = getUserActual(username);
        BankAccount userBankAccount = bankAccountService.appUserBankAccount(userActual);

        int updatedAccount = userActual.getAccountBalance() + updateSum;

        userActual.setAccountBalance(updatedAccount);
        userBankAccount.setAccountBalance(updatedAccount);

        String message = String.format("Account was update to %d by transfer %d",
                updatedAccount, updateSum);

        emailSender.send(userActual.getEmail(), message, "Updated account confirmation");

        return new ServerMsg(message);
    }

    @Transactional
    public ServerMsg userFounds(String username) {
        AppUser userActual = getUserActual(username);
        String message = String.format("Your current founds are %d", userActual.getAccountBalance());
        return new ServerMsg(message);
    }
}
