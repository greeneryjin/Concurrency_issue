package com.fisa.infra.board.domain;

import com.fisa.infra.account.domain.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@DynamicUpdate
@DynamicInsert
@Builder
@Entity
@Table(name = "boards")
public class Board {

	//게시글아이디
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "board_id")
	private Long boardId;
	
	//회원 객체
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="account_id")
	private Account account;

	//내용
	private String content;

	private Long viewCount;

	public void addAccount(Account account) {
		this.account = account;
	}

	public void updateViewCount(Long count) {
		this.viewCount += count;
	}

	public Object updateView(Long count) {
		this.viewCount += count;
		return this.viewCount;
	}
}
