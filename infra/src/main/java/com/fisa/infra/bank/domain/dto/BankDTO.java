package com.fisa.infra.bank.domain.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BankDTO {

    // account
    private String loginId;

    private String name;
    // board
    private Long id;
    private Long balance;
}
