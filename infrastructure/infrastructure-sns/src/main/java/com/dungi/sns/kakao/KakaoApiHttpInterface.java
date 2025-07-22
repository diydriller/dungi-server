package com.dungi.sns.kakao;

import com.dungi.sns.kakao.dto.KakaoInfoDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface KakaoApiHttpInterface {
    @GET("/v2/user/me")
    Call<KakaoInfoDto> getKakaoInfo(
            @Header("Authorization") String token,
            @Header("content-type") String type
    );
}
