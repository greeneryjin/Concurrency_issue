package com.fisa.infra.account.domain.dto;

import com.fisa.infra.account.domain.Account;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Builder
public class AccountDTO {
	//계정아이디
	private String loginId;

	private String pwd;

	private String name;
}
