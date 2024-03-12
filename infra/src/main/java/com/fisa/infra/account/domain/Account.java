package com.fisa.infra.account.domain;

import com.fisa.infra.board.domain.Board;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "accounts")
@Entity
public class Account  {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "account_id")
	private Long accountId;

	@Column(nullable = false, unique = true)
	private String loginId;

	@Column(nullable = false, unique = true)
	private String accountName;

	@Column(nullable = false)
	private String pwd;


	@Builder.Default
	@OneToMany(mappedBy = "account")
	private List<Board> board = new ArrayList<Board>();
	
	public static Account createAccount(String loginId, String pwd, String accountName) {
		return Account.builder()
				.loginId(loginId)
				.pwd(pwd)
				.accountName(accountName)
				.build();
	}
}


