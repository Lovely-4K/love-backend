package com.lovely4k.backend.authentication;

import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Role;
import com.lovely4k.backend.member.Sex;

import java.util.Map;

public record OAuthAttributes(
    Map<String, Object> attributes,
    String nameAttributeKey,
    String nickName,
    String email,
    String imageUrl,
    String ageRange,
    String sex
) {
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        OAuthAttributes oAuthAttributes;
        switch (registrationId) {
            case "kakao" -> oAuthAttributes = ofKakao(userNameAttributeName, attributes);
            case "naver" -> oAuthAttributes = ofNaver("id", attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth 입니다.");
        }

        return oAuthAttributes;
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> responses = (Map<String, Object>) attributes.get("response");

        return new OAuthAttributes(
            responses,
            userNameAttributeName,
            (String) responses.get("nickname"),
            (String) responses.get("email"),
            (String) responses.get("profile_image"),
            (String) responses.get("age"),
            (String) responses.get("gender")
        );
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profiles = (Map<String, Object>) kakaoAccount.get("profile");

        return new OAuthAttributes(
            attributes,
            userNameAttributeName,
            (String) properties.get("nickname"),
            (String) kakaoAccount.get("email"),
            (String) profiles.get("profile_image_url"),
            ((String) kakaoAccount.get("age_range")).replace('~', '-'),
            (String) kakaoAccount.get("gender")
        );
    }

    /*
     * 기존에 자사 서비스를 가입 한 적이 없는 사람들을 위한 회원 가입 1차 과정
     * 본격적인 활동은 Member 권한이 있어야 한다.
     * 회원에 필요한 값들을 채우기 전에는 GUEST 권한을 갖는다.
     */
    public Member toEntity() {
        return Member.builder()
            .nickname(nickName)
            .email(email)
            .imageUrl(imageUrl)
            .sex(Sex.of(sex))
            .ageRange(ageRange)
            .role(Role.GUEST)
            .build();
    }
}