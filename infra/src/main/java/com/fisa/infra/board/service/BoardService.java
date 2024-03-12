package com.fisa.infra.board.service;

import com.fisa.infra.account.domain.Account;
import com.fisa.infra.account.repository.jpa.AccountRepository;
import com.fisa.infra.board.domain.Board;
import com.fisa.infra.board.domain.dto.BoardDTO;
import com.fisa.infra.board.repository.jpa.BoardRepository;
import com.fisa.infra.board.repository.querydsl.QueryBoardRepository;
import com.fisa.infra.redis.DistributeLock;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Around;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;
	private final AccountRepository accountRepository;
	private final QueryBoardRepository queryBoardRepository;
	private final ModelMapper mapper = new ModelMapper();

	@Transactional
	public Long writeBoard (BoardDTO boardDTO) throws RuntimeException, IOException {
		Optional<Account> account = accountRepository.findAccountByLoginId(boardDTO.getLoginId());
		Board board = Board.builder()
				.content(boardDTO.getContent())
				.viewCount(0L)
				.build();
		//부모와 연관 관계
		board.addAccount(account.get());
		Board save = boardRepository.save(board);
		return save.getViewCount();
	}

	@Transactional(readOnly = true)
	public List<BoardDTO> getBoardById (){
		List<BoardDTO> board = queryBoardRepository.queryFindBoardById();
		return board.stream().map(b -> mapper.map(b, BoardDTO.class)).collect(Collectors.toList());
	}

	//JPA 더티 체킹 - 동시성 예제
	@Transactional
	public void updateBoardById1(Long id) {
		Optional<Board> board = boardRepository.findByBoardId(id);
		board.get().updateViewCount(1L);
	}

	//JPA 더티 체킹 사용하지 않고 DB에 바로 flush 1
	@Transactional
	public void updateBoardById2(Long id) {
		Optional<Board> board = boardRepository.findById(id);
		Long view = (Long) board.get().updateView(1L);
		boardRepository.updateBoardByViewCount(view, id);
	}

	//JPA 비관적 LOCK - 동시성 해결 예제 2
	@Transactional
	public void updateBoardById3(Long id) {
		Optional<Board> board = boardRepository.findById(id);
		board.get().updateViewCount(1L);
	}

	//레디스 분산 LOCK - 동시성 해결 예제 3
	@DistributeLock(key = "#lockName")
	public void updateBoardById4(Long id){
		Optional<Board> board = boardRepository.findByBoardId(id);
		board.get().updateViewCount(1L);
	}
}
