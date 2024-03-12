package com.fisa.infra.account.service;

import com.fisa.infra.account.domain.Account;
import com.fisa.infra.account.domain.dto.AccountDTO;
import com.fisa.infra.account.repository.jpa.AccountRepository;
import com.fisa.infra.role.domain.entity.AccountRole;
import com.fisa.infra.role.domain.entity.Role;
import com.fisa.infra.role.repository.jpa.AccountRoleRepository;
import com.fisa.infra.role.repository.querydsl.RoleRepositoryImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import java.util.Optional;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;
	private final RoleRepositoryImpl roleRepository;
	private final AccountRoleRepository accountRoleRepository;

	public Account accountCreate(AccountDTO accountdto) throws AccountException {

		Account account = Account.createAccount(accountdto.getLoginId(), accountdto.getPwd(), accountdto.getName());
		Account save = accountRepository.save(account);
//		// 1번 ROLE_ADMIN , 2번 ROLE_USER
//		Optional<Role> op = roleRepository.findById(save.getAccountId());
//		Role role = op.get();
//		AccountRole accountRole = AccountRole.createAccountRole(account, role);
//		accountRoleRepository.save(accountRole);
		return account;
	}
}
