package com.example.transfer.bankaccount;

import com.example.transfer.appuser.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class BankAccountService {

    private final static String ACCOUNT_NOT_FOUND_MSG = "User with id %d don't have account or didn't exists";
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountService(@Qualifier("bankAccountRepo") BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public void saveAppUserBankAccount(BankAccount bankAccount) {
        bankAccountRepository.save(bankAccount);
    }

    public BankAccount appUserBankAccount(AppUser appUser) {
        return bankAccountRepository.findByAppUserId(appUser.getId())
                .orElseThrow(() -> new NoSuchElementException(String.format(ACCOUNT_NOT_FOUND_MSG, appUser.getId())));
    }


    @Transactional
    public void transferFounds(AppUser from, AppUser to, int transfer) {
        BankAccount bankAccountUserFrom = appUserBankAccount(from);
        BankAccount bankAccountUserTo = appUserBankAccount(to);
        if (transfer > bankAccountUserFrom.getAccountBalance()) {
            throw new IllegalStateException("Insufficient funds");
        }
        int accountBalanceAfterDebiting = bankAccountUserFrom.getAccountBalance() - transfer;
        int accountBalanceAfterRecognition = bankAccountUserTo.getAccountBalance() + transfer;

        bankAccountUserFrom.setAccountBalance(accountBalanceAfterDebiting);
        bankAccountUserTo.setAccountBalance(accountBalanceAfterRecognition);

    }
}
