package com.fisa.infra.account.controller;

import com.fisa.infra.account.domain.dto.AccountDTO;
import com.fisa.infra.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/api")
public class AccountController {
	
	private final AccountService accountService;

	@GetMapping("/account/create")
	public String createForm(Model model) {
		model.addAttribute("accountDTO", new AccountDTO());
		return "entire/account/AccountSaveForm";
	}

	@PostMapping(value = "/account/create")
	public String accountCreate(AccountDTO accountDTO) throws AccountException {
		accountService.accountCreate(accountDTO);
		return "redirect:/api/bank/create";
	}
}
