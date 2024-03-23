# 동시성 이슈 테스트 

## 🖥️ SpringBoot, JPA, Redis 환경에서 동시성 제어 

### 📌 목적
- 동시성 제어의 원인과 이슈 제어 방법
- 낙관적 락
- 비관적 락
- 분산 락
  
### 🕰️ 개발 기간
2024.02 ~ 2024.03

## 프로젝트 구조

### ⚙️ 개발 환경
![image](https://github.com/greeneryjin/Concurrency_issue/assets/87289562/43fb7486-164a-41be-bdb5-1c1881de1fb7)

#### [Language]
<div> 
<img src="https://img.shields.io/badge/jdk 17-437291?style=flat&logo=openjdk&logoColor=white"/> 
</div>

#### [IDE]
<div> 
<img src="https://img.shields.io/badge/eclipse-2C2255?style=flat&logo=eclipseide&logoColor=white"/> 
<img src="https://img.shields.io/badge/intelij-000000?style=flat&logo=intellijidea&logoColor=white"/> 
</div>

#### [Framework]
<div> 
<img src="https://img.shields.io/badge/spring-6DB33F?style=flat&logo=Spring&logoColor=white"/> 
<img src="https://img.shields.io/badge/springBoot-6DB33F?style=flat&logo=Spring boot&logoColor=white"/>
<img src="https://img.shields.io/badge/spring Security-6DB33F?style=flat&logo=Spring Security&logoColor=white"/>
</div>


#### [Database]
<div>
<p> <img src="https://img.shields.io/badge/mysql-4479A1?style=flat&logo=mySql&logoColor=white"/></p>
</div>


## 계획
### 📌 목표
1. 낙관적락의 프로세스 이해와 제어 방법
2. 비관적락의 프로세스 이해와 제어 방법
3. 분산락의 프로세스 이해와 제어 방법

## 기능 구현
1. 유저 CRUD
2. 로그인/회원가입 - 스프링 시큐리티 사용
3. 은행 CRUD
4. 마이페이지

#### 동시성 발생 원인

공통 Controller
```JAVA
    @PutMapping("/bank/viewBalance/{id}")
    public String updateBank1(@PathVariable Long id) throws InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(() -> {
            log.info("Thread : {}", Thread.currentThread().getId());
            log.info("{************** 입금 시작 **************}");
            bankService.plusBankById(10000L, id);
        });
        executorService.submit(() -> {
            log.info("Thread : {}", Thread.currentThread().getId());
            log.info("{************** 출금 시작 및 종료 **************}");
            bankService.minusBankById(70000L, id);
        });
        executorService.submit(() -> {
            log.info("Thread : {}", Thread.currentThread().getId());
            log.info("{************** 입금 종료 **************}");
            bankService.plusBankById(2000L, id);
        });
        try {
        } finally {
            executorService.shutdown();
        }
        return "ok";
    }

```


```JAVA

    ## service
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
```

동시성 이슈 원인
![image](https://github.com/greeneryjin/Concurrency_issue/assets/87289562/b3e7c112-69f3-4f17-9314-103483ba7019)

동시성 원인은 갱신 분실로 먼저 실행된 트랜잭션의 결과를 나중에 실행된 트래잭션이 결과를 덮어쓰는 현상이 발생했습니다.


##### (1) 낙관적 락 

```java

    @Entity
    @Table(name = "banks")
    public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bank_id")
    private Long bankId;

    //회원 객체
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="account_id")
    private Account account;

    //잔액
    private Long balance;

    //버전 추가
    @Version 
    private Long version;
    }

    //controller
    simulateDelay(1000); -> 시간 추가 설정 
```

낙관적 락은 어플리케이션단에서 버전을 사용해서 동시성을 제어하고 동시성 이슈가 발생하지 않는 것을 가정해서 사용합니다. 

낙관적 락 프로세스

![image](https://github.com/greeneryjin/Concurrency_issue/assets/87289562/49dfc5ba-58b0-44b3-9553-6e73e5903f3b)

버전을 갱신하면서 동시성을 제어합니다.


낙관적 락 주의 사항

1. 데드락
2. 예외 발생 시 예외 처리 필수 및 롤백 처리

----

##### (2) 비관적 락 

```java
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Bank> findById(Long id);
```

비관적 락은 데이터베이스단에서 락을 사용해서 동시성을 제어하고 동시성 이슈가 발생할 것을 가정해서 사용합니다. 

비관적 락 프로세스

![image](https://github.com/greeneryjin/Concurrency_issue/assets/87289562/ead66428-5f6c-4392-a2e1-1aaf04bbc3f2)

배타락을 걸어서 동시성을 제어합니다. 


비관적 락 주의 사항

1. 데드락
2. 예외 발생 시 예외 처리 필수 및 롤백 처리

----

### 낙관적 락 VS 비관적 락

  낙관적 락
  
   충돌이 적은 곳에 사용하는 것을 권장
   
   롤백 처리 비용이 비싸고(network, Spring, Database) 버전을 위한 update쿼리가 추가로 발생 

비관적 락

   충돌이 많은 곳에 사용하는 것을 권장

   낙관적 락보다 롤백의 횟수가 적고 롤백 처리 비용이 비싸지 않음(batabase 영역)

### 하지만 낙관적 락과 비관적 락은 분산환경에서 제어하지 못합니다.

----

분산락을 통해 분산 환경에서 동시성을 제어합니다.

![image](https://github.com/greeneryjin/Concurrency_issue/assets/87289562/e94d3ef4-cfe1-44c5-a14c-ac68bbb9e520)

##### (3) 분산 락 

```JAVA
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributeLock {

    //분산락 key 이름
    String key(); // (1)

    //분산락 시간 단위
    TimeUnit timeUnit() default TimeUnit.SECONDS; // (2)

    //분산락을 얻기 위한 대기 시간,
    //다른 스레드나 프로세스가 해당 락을 풀어줘야 락을 획득할 수 있음
    long waitTime() default 5L; // (3)

    //분삭락 유지 시간으로 락을 획득한 이후에 해당 시간동안 분산락이 유지됨
    //시간이 종료되면 자동으로 락이 해체됨
    long leaseTime() default 3L; // (4)
}

    //서비스 계층에서 락 추가 
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
```

분산 락

분산 락 프로세스

![image](https://github.com/greeneryjin/fisa_coding_test/assets/87289562/3b8bedb0-9393-4707-a4d3-5432d6e160da)

분산 락을 사용할 때 순서 주의

1. 레디스에 락을 획득

2. DB 트랜잭션 커밋

3. 락 해제

   -> 순서가 락을 먼저 획득하고 트랜잭션 처리를 해야 동시성 이슈가 발생하지 않습니다.

   트랜잭션을 먼저 적용하고 락을 획득하면 DB 트랜잭션 격리 수준에 의해 동시성 이슈가 발생할 수 있습니다.

   만약 MySQL을 사용하면 REPETABLE READ(트랜잭션 시작되기 전, 커밋된 내용만 조회 가능)의 격리 수준으로

   DB 조회가 가능하게 되기 때문에 A가 트랜잭션 획득하고 락을 획득하고 커밋한 결과 값과 B가 트랜잭션 조회하고

    해제된 락을 획득 후, 커밋하면 결과 값에서 동시성 이슈가 발생하게 됩니다. 


분산 락 주의 사항

1. 데드락
2. 예외 발생 시 예외 처리 필수 및 롤백 처리
