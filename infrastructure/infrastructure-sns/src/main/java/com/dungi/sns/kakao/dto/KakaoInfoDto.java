package com.dungi.sns.kakao.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KakaoInfoDto {
    Account kakao_account;

    @Data
    public class Account{
        String email;
    }
}
