package com.dungi.sns.kakao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


@Configuration
public class KakaoConfig {
    private static final String KAKAO_API_URL = "https://kapi.kakao.com";
    private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com";

    @Bean
    public KakaoApiHttpInterface kakaoApiHttpInterface(){
        return new Retrofit.Builder()
                .baseUrl(KAKAO_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KakaoApiHttpInterface.class);
    }

    @Bean
    public KakaoAuthHttpInterface kakaoAuthHttpInterface(){
        return new Retrofit.Builder()
                .baseUrl(KAKAO_AUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KakaoAuthHttpInterface.class);
    }
}
