package com.fisa.infra.bank.controller;

import com.fisa.infra.bank.domain.dto.BankDTO;
import com.fisa.infra.bank.service.BankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class BankController {

    private final BankService bankService;

    @GetMapping("/bank/create")
    public String createForm(Model model) {
        model.addAttribute("bankDTO", new BankDTO());
        return "entire/bank/bankSaveForm";
    }

    @PostMapping(value = "/bank/create")
    public String writeBoard(@ModelAttribute("bankDTO") BankDTO bankDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() == "anonymousUser") {
            bankService.writeBank(bankDTO);
        }
        //boardService.writeBoard(boardDTO);
        return "redirect:/api/bank/bankAll";
    }

    @GetMapping(value = "/bank/bankAll")
    public String getBoardById(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //로그인 안 한 사용자
        if (authentication.getPrincipal() == "anonymousUser") {
            List<BankDTO> bankDto = bankService.getBankById();
            model.addAttribute("bankDto", bankDto);
            return "entire/bank/bankAllForm";
        }
        //로그인 한 사용자
        else {
            List<BankDTO> bankDto = bankService.getBankById();
            model.addAttribute("bankDto", bankDto);
            return "entire/bank/bankAllForm";
        }
    }

}