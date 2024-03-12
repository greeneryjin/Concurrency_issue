package com.fisa.infra.bank.domain;

import com.fisa.infra.account.domain.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@DynamicUpdate
@DynamicInsert
@Builder
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

    @Version
    private Long version;

    public void addAccount(Account account) {
        this.account = account;
    }

    public void deposit(Long balance) {
        this.balance += balance;
    }

    public void withdraw(Long balance) {
        this.balance -= balance;
    }

    public Long getDeposit(Long balance) {
        this.balance += balance;
        return this.balance;
    }

    public Long getWithdraw(Long balance) {
        this.balance -= balance;
        return this.balance;
    }
}
