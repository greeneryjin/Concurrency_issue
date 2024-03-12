//package com.fisa.infra.board.service;
//
//import com.fisa.infra.board.domain.Board;
//import com.fisa.infra.board.repository.jpa.BoardRepository;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//
//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//class BoardServiceTest {
//
//    @Autowired
//    BoardRepository boardRepository;
//
//    @Autowired
//    BoardService boardService;
//
//    //@Test
//    @Rollback(value = false)
//    public void 동시_요청_확인() throws InterruptedException {
//        int threadCount = 100; //100명이 조회를 함
//
//        //스레드 30개로 처리
//        ExecutorService executorService = Executors.newFixedThreadPool(30);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        for(int i=0; i < threadCount; i++){
//            executorService.submit(()->{
//                try{
//                    boardService.updateBoardById1(1L);
//                } finally {
//                    latch.countDown(); //완료되었음을 알림
//                }
//            });
//        }
//        latch.await();
//        Board board = boardRepository.findById(1L).orElseThrow();
//        assertEquals(101L, board.getViewCount());
//    }
//
//    @Test
//    @Rollback(value = false)
//    public void 비관적락_동시_요청_해결() throws InterruptedException {
//        int threadCount = 100; //100명이 조회를 함
//
//        //스레드 30개로 처리
//        ExecutorService executorService = Executors.newFixedThreadPool(30);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        for(int i=0; i < threadCount; i++){
//            executorService.submit(()->{
//                try{
//                    boardService.updateBoardById2(2L);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                } finally {
//                    latch.countDown(); //완료되었음을 알림
//                }
//            });
//        }
//        latch.await();
//        Board board = boardRepository.findById(2L).orElseThrow();
//        assertEquals(100L, board.getViewCount());
//    }
//}