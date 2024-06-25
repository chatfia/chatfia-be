package com.project.chatfiabe.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseResponseStatus {

    /**
     * 1. 요청에 성공한 경우(2000)
     */
    // 0. 공통
    SUCCESS(true, 2000, "요청에 성공하였습니다."),

    // 1-1. 회원가입 / 로그인 / 로그아웃 / 회원탈퇴
    REGISTER_ACCOUNT_SUCCESS(true, 2100, "회원가입이 완료되었습니다"),
    LOGIN_SUCCESS(true, 2101, "로그인이 완료되었습니다."),
//    KAKAO_LOGIN_SUCCESS(true, 2102, "카카오 로그인이 완료되었습니다."),
//    GOOGLE_LOGIN_SUCCESS(true, 2103, "구글 로그인이 완료되었습니다."),
    LOGOUT_SUCCESS(true, 2104, "로그아웃이 완료되었습니다."),
    DELETE_ACCOUNT_SUCCESS(true, 2105, "회원탈퇴가 완료되었습니다."),


    /**
     * 2. 클라이언트 에러(4000)
     */
    // 0. 공통
    BAD_REQUEST(false, 4000, "잘못된 요청입니다."),

    // 2-1. 회원가입 / 로그인 / 로그아웃 / 회원탈퇴
    EMAIL_ALREADY_EXISTS(false, 4100, "이미 가입된 이메일입니다."),
    LOGIN_FAILED(false, 4101, "로그인에 실패했습니다. 다시 로그인을 진행해 주세요."),
    KAKAO_LOGIN_FAILED(false, 4102, "카카오 로그인에 실패했습니다. 다시 로그인을 진행해 주세요."),
    GOOGLE_LOGIN_FAILED(false, 4103, "구글 로그인에 실패했습니다. 다시 로그인을 진행해 주세요."),
    USER_NOT_FOUND(false, 4104, "가입된 유저 정보가 없습니다."),
    PASSWORD_MISMATCH(false, 4105, "비밀번호가 일치하지 않습니다."),
    LOGOUT_FAILED(false, 4106, "로그아웃에 실패했습니다."),
    DELETE_ACCOUNT_FAILED(false, 4107, "회원탈퇴에 실패했습니다."),

    // 2-2. 인가 / 인증
    INVALID_BEARER_GRANT_TYPE(false, 4200, "Bearer 타입이 아닙니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_FOUND(false, 4201, "JWT 토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(false, 4202, "JWT 토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(false, 4203, "JWT 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(false, 4204, "로그인 인증이 만료되었습니다. 다시 로그인을 진행해 주세요."),
    AUTHENTICATION_FAILED(false, 4205, "인증에 실패했습니다"),
    NOT_FOUND_USERDETAILS(false, 4206, "유저의 정보를 찾을 수 없습니다."),

    /**
     * 3. 서버 에러(5000)
     */
    // 0. 공통
    INTERNAL_SERVER_ERROR(false, 5000, "서버 내부 에러가 발생했습니다."),
    UNEXPECTED_ERROR(false, 5001, "예상치 못한 에러가 발생했습니다."),
    FAIL_TO_ENCODING(false, 5002, "요청 인코딩에 실패했습니다."),
    FAIL_TO_JSON(false, 5003, "JSON 파싱 에러가 발생했습니다."),

    // 3-1. 이메일
    EMAIL_SEND_FAILED(false, 5100, "이메일 전송에 실패했습니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;
    private HttpStatus httpStatus;

    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

    BaseResponseStatus(boolean isSuccess, int code, String message, HttpStatus httpStatus) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}