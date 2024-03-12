package com.fisa.infra.board.controller;

import com.fisa.infra.board.domain.dto.BoardDTO;
import com.fisa.infra.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {

	private final BoardService boardService;

	@GetMapping("/board/create")
	public String createForm(Model model) {
		model.addAttribute("boardDTO", new BoardDTO());
		return "entire/board/boardSaveForm";
	}

	@PostMapping(value = "/board/create")
	public String writeBoard(@ModelAttribute("boardDTO") BoardDTO boardDTO) throws IOException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if(authentication.getPrincipal() == "anonymousUser") {
			boardService.writeBoard(boardDTO);
		}
		//boardService.writeBoard(boardDTO);
		return "redirect:/api/board/boardAll";
	}

	@GetMapping(value = "/board/boardAll")
	public String getBoardById(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//로그인 안 한 사용자
		if(authentication.getPrincipal() == "anonymousUser") {
			List<BoardDTO> board = boardService.getBoardById();
			model.addAttribute("board", board);
			return "entire/board/boardAllForm";
		}
		//로그인 한 사용자
		else {
			List<BoardDTO> board = boardService.getBoardById();
			model.addAttribute("board", board);
			return "entire/board/boardAllForm";
		}
	}

	public ExecutorService threadNew() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		return executorService;
	}

	//동시성 이슈 발생
	@PutMapping("/board/viewCount1/{id}")
	public String updateView1(@PathVariable Long id) throws InterruptedException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//로그인 안 한 사용자
		if(authentication.getPrincipal() == "anonymousUser") {

			//조회수 100회
			int threadCount = 50;

			//스레드 30개 생성
			ExecutorService executorService = threadNew();
			CountDownLatch latch = new CountDownLatch(threadCount);

			for(int i=0; i < threadCount; i++){
				executorService.submit(()->{
					try{
						boardService.updateBoardById1(id);
					} finally {
						latch.countDown(); //완료되었음을 알림
					}
				});
			}
			latch.await();
			return "entire/board/boardOneForm";
		}
		//로그인 한 사용자
		else {
			boardService.updateBoardById1(id);
			return "entire/board/boardOneForm";
		}
	}

	//동시성 해결 2
	@PutMapping("/board/viewCount2/{id}")
	public String updateView2(@PathVariable Long id) throws InterruptedException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//로그인 안 한 사용자
		if(authentication.getPrincipal() == "anonymousUser") {

			int threadCount = 50;
			ExecutorService executorService = threadNew();
			CountDownLatch latch = new CountDownLatch(threadCount);

			for(int i = 0; i < threadCount; i++){
				executorService.submit(()->{
					try{
						boardService.updateBoardById2(id);
					} finally {
						latch.countDown(); //완료되었음을 알림
					}
				});
			}
			latch.await();
			return "entire/board/boardOneForm";
		}
		//로그인 한 사용자
		else {
			boardService.updateBoardById2(id);
			return "entire/board/boardOneForm";
		}
	}

	//동시성 해결 3
	@PutMapping("/board/viewCount3/{id}")
	public String updateView4(@PathVariable Long id) throws InterruptedException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//로그인 안 한 사용자
		if(authentication.getPrincipal() == "anonymousUser") {

			int threadCount = 50;
			ExecutorService executorService = threadNew();
			CountDownLatch latch = new CountDownLatch(threadCount);

			for(int i = 0; i < threadCount; i++){
				executorService.submit(()->{
					try{
						boardService.updateBoardById3(id);
					} finally {
						latch.countDown(); //완료되었음을 알림
					}
				});
			}
			latch.await();
			return "entire/board/boardOneForm";
		}
		//로그인 한 사용자
		else {
			boardService.updateBoardById2(id);
			return "entire/board/boardOneForm";
		}
	}

	//동시성 해결 5
	@PutMapping("/board/viewCount4/{id}")
	public String updateView5(@PathVariable Long id) throws InterruptedException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//로그인 안 한 사용자
		if(authentication.getPrincipal() == "anonymousUser") {

			int threadCount = 50;
			ExecutorService executorService = threadNew();
			CountDownLatch latch = new CountDownLatch(threadCount);

			for(int i = 0; i < threadCount; i++){
				executorService.submit(()->{
					try{
						boardService.updateBoardById4(id);
					} finally {
						latch.countDown(); //완료되었음을 알림
					}
				});
			}
			latch.await();
			return "entire/board/boardOneForm";
		}
		//로그인 한 사용자
		else {
			boardService.updateBoardById2(id);
			return "entire/board/boardOneForm";
		}
	}
}
