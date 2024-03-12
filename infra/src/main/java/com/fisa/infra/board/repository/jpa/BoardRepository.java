package com.fisa.infra.board.repository.jpa;

import com.fisa.infra.board.domain.Board;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Long> {

    List<Board> findAll();
    Optional<Board> findByBoardId(Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Board> findById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update Board b set b.viewCount =:viewCount where b.boardId =:boardId")
    void updateBoardByViewCount(Long viewCount, Long boardId);
}
