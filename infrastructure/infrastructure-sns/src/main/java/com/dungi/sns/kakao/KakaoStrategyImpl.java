package com.dungi.sns.kakao;

import com.dungi.common.exception.NotFoundException;
import com.dungi.common.value.SnsProvider;
import com.dungi.core.integration.sns.SnsStrategy;
import com.dungi.sns.kakao.dto.KakaoInfoDto;
import com.dungi.sns.kakao.dto.SnsTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.util.Optional;

import static com.dungi.common.response.BaseResponseStatus.NOT_EXIST_USER;

@Component
@RequiredArgsConstructor
public class KakaoStrategyImpl implements SnsStrategy {
    @Value("${kakao.accountId}")
    private String kakaoAccountId;
    @Value("${kakao.secret}")
    private String kakaoSecret;
    @Value("${kakao.callbackUri}")
    private String kakaoCallbackUri;

    private final KakaoApiHttpInterface kakaoApiService;
    private final KakaoAuthHttpInterface kakaoAuthService;

    @Override
    public String getSnsEmail(String token) throws Exception {
        var kakaoInfo = fetchKakaoInfo(token);
        return Optional.ofNullable(kakaoInfo)
                .map(KakaoInfoDto::getKakao_account)
                .map(KakaoInfoDto.Account::getEmail)
                .orElseThrow(() -> new NotFoundException(NOT_EXIST_USER));
    }

    @Override
    public String getSnsToken(String code) throws Exception {
        var kakaoToken = fetchKakaoToken(code);
        return Optional.ofNullable(kakaoToken)
                .map(SnsTokenDto::getAccess_token)
                .orElseThrow(() -> new NotFoundException(NOT_EXIST_USER));
    }

    @Override
    public SnsProvider getServiceType() {
        return SnsProvider.KAKAO;
    }

    private KakaoInfoDto fetchKakaoInfo(String token) throws Exception {
        Call<KakaoInfoDto> retrofitCall = kakaoApiService.getKakaoInfo(
                "Bearer " + token, "application/x-www-form-urlencoded"
        );
        Response<KakaoInfoDto> response = retrofitCall.execute();

        if (!response.isSuccessful() || response.body() == null) {
            throw new NotFoundException(NOT_EXIST_USER);
        }
        return response.body();
    }

    private SnsTokenDto fetchKakaoToken(String code) throws Exception {
        Call<SnsTokenDto> retrofitCall = kakaoAuthService.getKakaoToken(
                "authorization_code", kakaoAccountId, kakaoCallbackUri, code, kakaoSecret
                , "application/x-www-form-urlencoded"
        );
        Response<SnsTokenDto> response = retrofitCall.execute();

        if (!response.isSuccessful() || response.body() == null) {
            throw new NotFoundException(NOT_EXIST_USER);
        }
        return response.body();
    }
}
