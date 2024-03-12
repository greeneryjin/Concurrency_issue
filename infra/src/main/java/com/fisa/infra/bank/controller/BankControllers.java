package com.fisa.infra.bank.controller;

import com.fisa.infra.bank.service.BankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BankControllers {

    private final BankService bankService;

    /**
     * 첫 계좌 개설 100,000 입금
     * thread 1 - 은행 이자 수익 10,000
     * thread 2 - 계좌 이체 출금 70,000
     * thread 3 - 주식 수익 2,000
     * 계좌 잔액  - 42,000
     * */
    public ExecutorService threadNew() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        return executorService;
    }

    //은행 동시성 이슈
    @PutMapping("/bank/viewBalance/{id}")
    public String updateBank1(@PathVariable Long id) throws InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //로그인 안 한 사용자
        if(authentication.getPrincipal() == "anonymousUser") {

            int threadCount = 3;
            ExecutorService executorService = threadNew();
            //CountDownLatch latch = new CountDownLatch(threadCount);

            executorService.submit(() -> {
                log.info("Thread : {}", Thread.currentThread().getId());
                log.info("{************** 입금 시작 **************}");
                bankService.plusBankById(10000L, id);
                // latch.countDown();
            });

            executorService.submit(() -> {
                log.info("Thread : {}", Thread.currentThread().getId());
                log.info("{************** 출금 시작 및 종료 **************}");
                bankService.minusBankById(70000L, id);
                //latch.countDown();
            });

            executorService.submit(() -> {
                log.info("Thread : {}", Thread.currentThread().getId());
                log.info("{************** 입금 종료 **************}");
                bankService.plusBankById(2000L, id);
                //latch.countDown();
            });

            try {
                // latch.await();
            } finally {
                executorService.shutdown();
            }
            return "ok";
        }
        else {
            return "entire/board/boardOneForm";
        }
    }

    // 강제로 지연을 발생시키는 메서드
    private void simulateDelay(int count) {
        try {
            Thread.sleep(count); // 1초 지연
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //동시성 해결 1 - 낙관적 락
    @PutMapping("/bank/viewBalance1/{id}")
    public String updateBank2(@PathVariable Long id) throws InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() == "anonymousUser") {

            ExecutorService executorService = threadNew();
            executorService.submit(() -> {
                simulateDelay(1000);
                log.info("Thread : {}", Thread.currentThread().getId());
                bankService.plusBank1(10000L, id);
            });

            executorService.submit(() -> {
                simulateDelay(2000);
                log.info("Thread : {}", Thread.currentThread().getId());
                bankService.minusBank1(70000L, id);
            });

            executorService.submit(() -> {
                simulateDelay(3000);
                log.info("Thread : {}", Thread.currentThread().getId());
                log.info("{************** 입금 종료 **************}");
                bankService.plusBank1(2000L, id);
            });

            //shutdown 작업 큐의 작업까지 모두 처리 후 종료하고 새로운 작업들을 더 이상 받아들이지 않음
            executorService.shutdown();
            //CountDownLatch의 카운트가 0이 될때까지 대기해서 main 스레드가 실행되지 못하도록 대기하도록 만듬
            //latch.await();
            return "ok";
        }
        else {
            return "entire/board/boardOneForm";
        }
    }
    //동시성 해결 2 - 비관적 락
    @PutMapping("/bank/viewBalance2/{id}")
    public String updateBank3(@PathVariable Long id) throws InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() == "anonymousUser") {

            int threadCount = 3;
            ExecutorService executorService = threadNew();
            // CountDownLatch latch = new CountDownLatch(threadCount);

            executorService.submit(() -> {
                log.info("Thread : {}", Thread.currentThread().getId());
                log.info("{************** 입금 시작 **************}");
                bankService.plusBank2(10000L, id);
                //  latch.countDown();
            });

            executorService.submit(() -> {
                log.info("Thread : {}", Thread.currentThread().getId());
                log.info("{************** 출금 시작 **************}");
                bankService.minusBank2(70000L, id);
                // latch.countDown();
            });

            executorService.submit(() -> {
                log.info("Thread : {}", Thread.currentThread().getId());
                log.info("{************** 입금 시작 **************}");
                bankService.plusBank2(2000L, id);
                // latch.countDown();
            });

            try {
                //latch.await();
            } finally {
                executorService.shutdown();
            }
            return "ok";
        }
        //로그인 한 사용자
        else {
            bankService.plusBank2(10000L, id);
            bankService.minusBank2(70000L, id);
            bankService.plusBank2(30000L, id);
            return "entire/board/boardOneForm";
        }
    }
    //동시성 해결 3 - 분산 락
    @PutMapping("/bank/viewBalance3/{id}")
    public String updateBank4(@PathVariable Long id) throws InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() == "anonymousUser") {

            int threadCount = 3;
            ExecutorService executorService = threadNew();
            //CountDownLatch latch = new CountDownLatch(threadCount);

            executorService.submit(() -> {
                log.info("Thread : {}", Thread.currentThread().getId());
                log.info("{************** 입금 시작 **************}");
                bankService.plusBank3(10000L, id);
                //latch.countDown();
            });

            executorService.submit(() -> {
                log.info("Thread : {}", Thread.currentThread().getId());
                log.info("{************** 출금 시작 **************}");
                bankService.minusBank3(70000L, id);
                //latch.countDown();
            });

            executorService.submit(() -> {
                log.info("Thread : {}", Thread.currentThread().getId());
                log.info("{************** 입금 시작 **************}");
                bankService.plusBank3(2000L, id);
                //latch.countDown();
            });

            try {
                // latch.await();
            } finally {
                executorService.shutdown();
            }
            return "ok";
        }
        //로그인 한 사용자
        else {
            bankService.plusBank3(10000L, id);
            bankService.minusBank3(70000L, id);
            bankService.plusBank3(30000L, id);
            return "entire/board/boardOneForm";
        }
    }
}

//            for (int i = 0; i < threadCount; i++) {
//        executorService.submit(() -> {
//        log.info("Thread: {}", Thread.currentThread().getId());
//        bankService.plusBank1(100L, id);
//        });
//        }

