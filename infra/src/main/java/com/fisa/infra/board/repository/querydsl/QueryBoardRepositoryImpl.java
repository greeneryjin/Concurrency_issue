package com.fisa.infra.board.repository.querydsl;

import com.fisa.infra.account.domain.QAccount;
import com.fisa.infra.board.domain.QBoard;
import com.fisa.infra.board.domain.dto.BoardDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QueryBoardRepositoryImpl implements QueryBoardRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BoardDTO> queryFindBoardById() {
        QBoard board = QBoard.board;
        QAccount account = QAccount.account;

        List<BoardDTO> boardDTOList = queryFactory
                .select(Projections.constructor(BoardDTO.class,
                        account.loginId,
                        board.boardId,
                        board.content,
                        board.viewCount
                ))
                .from(board)
                .join(board.account, account)
                .fetch();
        return boardDTOList;
    }

}
