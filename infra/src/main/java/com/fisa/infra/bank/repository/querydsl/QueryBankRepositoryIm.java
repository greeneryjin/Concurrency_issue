package com.fisa.infra.bank.repository.querydsl;

import com.fisa.infra.account.domain.QAccount;
import com.fisa.infra.bank.domain.QBank;
import com.fisa.infra.bank.domain.dto.BankDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QueryBankRepositoryIm implements QueryBankRepository{

    private final JPAQueryFactory queryFactory;
    @Override
    public List<BankDTO> queryFindBoardById() {

        QBank bank = QBank.bank;
        QAccount account = QAccount.account;

        List<BankDTO> bankDTOList = queryFactory
                .select(
                        Projections.constructor(
                                BankDTO.class,
                                account.loginId,
                                account.accountName,
                                bank.bankId,
                                bank.balance))
                .from(bank)
                .join(bank.account, account)
                .fetch();
        return bankDTOList;
    }
}
