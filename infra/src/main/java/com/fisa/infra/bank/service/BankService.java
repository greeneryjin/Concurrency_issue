package com.fisa.infra.bank.service;

import com.fisa.infra.account.domain.Account;
import com.fisa.infra.account.repository.jpa.AccountRepository;
import com.fisa.infra.bank.domain.Bank;
import com.fisa.infra.bank.domain.dto.BankDTO;
import com.fisa.infra.bank.repository.jpa.BankRepository;
import com.fisa.infra.bank.repository.querydsl.QueryBankRepository;
import com.fisa.infra.redis.DistributeLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankService {

    private final AccountRepository accountRepository;
    private final BankRepository bankRepository;
    private final QueryBankRepository queryBankRepository;
    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Transactional
    public void writeBank(BankDTO bankDTO) {
        Optional<Account> account = accountRepository.findAccountByLoginId(bankDTO.getLoginId());
        Bank bank = Bank.builder()
                .balance(100000L)
                .build();
        bank.addAccount(account.get());
        bankRepository.save(bank);
    }

    @Transactional
    public List<BankDTO> getBankById() {
        List<BankDTO> bankDTOList = queryBankRepository.queryFindBoardById();
        return bankDTOList.stream().map(b -> mapper.map(b, BankDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public void plusBankById(Long pay, Long id) {
        log.info("서비스 단 Thread : {}", Thread.currentThread().getId());
        Optional<Bank> bank = bankRepository.findByBankId(id);
        bank.get().deposit(pay);
        log.info("서비스 반영 금액 : {}", bank.get().getBalance());
        log.info("서비스 단 Thread 작업 완료: {}", Thread.currentThread().getId());
    }

    @Transactional
    public void minusBankById(Long pay, Long id) {
        log.info("서비스 단 Thread : {}", Thread.currentThread().getId());
        Optional<Bank> bank = bankRepository.findByBankId(id);
        Long withdraw = bank.get().getWithdraw(pay);
        bankRepository.updateBankByBalance(withdraw, id);
    }

    public void plusBank1(Long pay, Long id) {

        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);

        try {
            log.info("*************** 입금 시작  ***************");
            Optional<Bank> bank = bankRepository.findByBankId(id);
            log.info("[버전 : {}] [ pay {}] [서비스 단 Thread : {}]", bank.get().getVersion(), pay, Thread.currentThread().getId());
            log.info("[버전 : {}] [ pay {}] [서비스 단 Thread : {}]", bank.get().getVersion(), pay, Thread.currentThread().getId());
            bank.get().deposit(pay);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    public void minusBank1(Long pay, Long id) {

        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);

        try {
            log.info("*************** 입금 시작  ************** ");
            Optional<Bank> bank = bankRepository.findByBankId(id);
            log.info("[버전 : {}] [ pay {}] [서비스 단 Thread : {}]", bank.get().getVersion(), pay, Thread.currentThread().getId());
            bank.get().withdraw(pay);
            log.info("*************** 입금 종료  ************** ");
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    public void plusBank2(Long pay, Long id) {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);

        try {
            Optional<Bank> bank = bankRepository.findById(id);
            bank.get().deposit(pay);
            log.info("[pay {}] [서비스 단 Thread : {}]", pay, Thread.currentThread().getId());
            log.info("서비스 반영 금액 : {}", bank.get().getBalance());
            log.info("{************** 서비스 단 입금 완료 **************}");
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    public void minusBank2(Long pay, Long id) {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);

        try {
            Optional<Bank> bank = bankRepository.findById(id);
            bank.get().withdraw(pay);
            log.info("[pay {}] [서비스 단 Thread : {}]", pay, Thread.currentThread().getId());
            log.info("서비스 반영 금액 : {}", bank.get().getBalance());
            log.info("{************** 서비스 단 출금 완료 **************}");
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
    @DistributeLock(key = "#lockName")
    public void plusBank3(Long pay, Long id) {
        try {
            Optional<Bank> bank = bankRepository.findById(id);
            bank.get().deposit(pay);
            log.info("[pay {}] [서비스 단 Thread : {}]", pay, Thread.currentThread().getId());
            log.info("서비스 반영 금액 : {}", bank.get().getBalance());
            log.info("{************** 서비스 단 입금 완료 **************}");
        } catch (Exception e) {
            // 롤백을 수행
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @DistributeLock(key = "#lockName")
    public void minusBank3(Long pay, Long id) {
        try {
            Optional<Bank> bank = bankRepository.findById(id);
            bank.get().withdraw(pay);
            log.info("[pay {}] [서비스 단 Thread : {}]", pay, Thread.currentThread().getId());
            log.info("서비스 반영 금액 : {}", bank.get().getBalance());
            log.info("{************** 서비스 단 출금 완료 **************}");
        } catch (Exception e) {
            // 롤백을 수행
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }
}

//    @Transactional(isolation = Isolation.READ_COMMITTED)
//    public void plusBank1(Long pay, Long id) {
//
//        log.info("*************** 입금 시작  ************** ");
//        Bank bank = bankRepository.findBankByBankId(id);
//        log.info("[버전 : {}] [ pay {}] [서비스 단 Thread : {}]", bank.getVersion(), pay, Thread.currentThread().getId());
//        bank.deposit(pay);
//    }

//    @Transactional
//    public void minusBank1(Long pay, Long id) {
//
//        log.info("*************** 출금 시작  ************** ");
//        Bank bank = bankRepository.findBankByBankId(id);
//        log.info("[버전 : {}] [ pay {}] [서비스 단 Thread : {}]", bank.getVersion(), pay, Thread.currentThread().getId());
//        // 낙관적 락은 엔티티를 수정할 때마다 버전이 변경됨
//        log.info("*************** 출금 UPDATE  ************** ");
//        bank.withdraw(pay);
//    }