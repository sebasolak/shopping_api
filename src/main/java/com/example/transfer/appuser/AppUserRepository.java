package com.example.transfer.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository("appUserRepo")
@Transactional(readOnly = true)
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    Optional<AppUser> findByLogin(String login);

    AppUser findAppUserByNameAndSurname(String name, String surname);

//    @Modifying
//    @Transactional
//    @Query(value = "UPDATE app_user SET account_balance = ?3 WHERE name LIKE ?1 AND surname LIKE ?2", nativeQuery = true)
//    int updateAppUserAccountBalance(String name, String surname, int accountBalance);
}
