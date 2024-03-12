package com.fisa.infra.bank.repository.jpa;

import com.fisa.infra.bank.domain.Bank;
import com.fisa.infra.board.domain.Board;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {



    Optional<Bank> findByBankId(Long id);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT b FROM Bank b WHERE b.bankId = :bankId")
    Optional<Bank> findByIdForUpdate(@Param("bankId") Long bankId);

    @Lock(LockModeType.OPTIMISTIC)
    Bank findBankByBankId(Long bankId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Bank> findById(Long id);

    //JPA 체킹 기능 안씀
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update Bank b set b.balance =:pay where b.bankId =:bankId")
    void updateBankByBalance(@Param("pay")Long pay, @Param("bankId") Long bankId);
}
