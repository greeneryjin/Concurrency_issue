package com.fisa.infra.bank.repository.querydsl;

import com.fisa.infra.bank.domain.dto.BankDTO;
import com.fisa.infra.board.domain.dto.BoardDTO;

import java.util.List;

public interface QueryBankRepository {

    List<BankDTO> queryFindBoardById();
}
