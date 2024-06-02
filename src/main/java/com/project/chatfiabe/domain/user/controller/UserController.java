package com.project.chatfiabe.domain.user.controller;

import com.project.chatfiabe.domain.user.dto.*;
import com.project.chatfiabe.domain.user.entity.TokenType;
import com.project.chatfiabe.domain.user.jwt.JwtProvider;
import com.project.chatfiabe.domain.user.security.UserDetailsImpl;
import com.project.chatfiabe.domain.user.service.AuthService;
import com.project.chatfiabe.domain.user.service.TokenBlackListService;
import com.project.chatfiabe.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {
    private final JwtProvider jwtProvider;
    private final AuthService authService;
    private final UserService userService;
    private final TokenBlackListService tokenBlackListService;

    /**
     * 회원가입
     * @param requestDto 회원가입 요청 DTO
     * @return 회원가입 결과와 HTTP 상태 코드 200 (OK)
     */
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        return ResponseEntity.ok(userService.signup(requestDto));
    }

    /**
     * 로그아웃
     * @param request HTTP 요청 객체
     * @return HTTP 상태 코드 204 (No Content)
     */
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        tokenBlackListService.addToBlackList(
                jwtProvider.getJwtFromHeader(request, TokenType.ACCESS),
                jwtProvider.getJwtFromHeader(request, TokenType.REFRESH)
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 토큰 리프레시
     * @param request HTTP 요청 객체
     * @return 새로 발급된 액세스 토큰과 HTTP 상태 코드 200 (OK)
     */
    @GetMapping("/refresh")
    public ResponseEntity<String> refresh(HttpServletRequest request) {
        String accessToken = authService.refreshAccessToken(jwtProvider.getJwtFromHeader(request, TokenType.REFRESH));
        return ResponseEntity.ok(accessToken);
    }

    /**
     * 블랙리스트 초기화
     * @return HTTP 상태 코드 204 (No Content)
     */
    @GetMapping("/blacklist/reset")
    public ResponseEntity<Void> resetBlackList() {
        tokenBlackListService.removeExpiredTokens();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 닉네임 수정
     * @param userDetails 인증된 사용자 정보
     * @param requestDto  닉네임 수정 요청 DTO
     * @return 수정된 사용자 정보와 HTTP 상태 코드 200 (OK)
     */
    @PatchMapping("/info/nickname")
    public ResponseEntity<UserInfoResponseDto> updateNickname(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserInfoRequestDto requestDto) {
        UserInfoResponseDto updatedUser = userService.updateUserInfo(userDetails.getUser(), requestDto.getNickname());
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 비밀번호 변경
     * @param userDetails 인증된 사용자 정보
     * @param requestDto  비밀번호 변경 요청 DTO
     * @return 비밀번호 변경 완료 메시지와 HTTP 상태 코드 200 (OK)
     */
    @PatchMapping("/info/password")
    public ResponseEntity<String> updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserInfoRequestDto requestDto) {
        userService.updatePassword(userDetails.getUser(), requestDto);
        return ResponseEntity.ok("비밀번호 수정이 완료되었습니다.");
    }

    /**
     * 회원 정보 조회
     * @param userDetails 인증된 사용자 정보
     * @return 사용자 정보와 HTTP 상태 코드 200 (OK)
     */
    @GetMapping
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserInfoResponseDto userInfoResponseDto = userService.getUserInfo(userDetails.getUser());
        return ResponseEntity.ok(userInfoResponseDto);
    }

    /**
     * 회원 탈퇴
     * @param userDetails 인증된 사용자 정보
     * @param requestDto  회원 탈퇴 요청 DTO
     * @return 회원 탈퇴 완료 메시지와 HTTP 상태 코드 200 (OK)
     */
    @DeleteMapping("/info")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody DeleteUserInfoRequestDto requestDto) {
        userService.deleteUser(userDetails.getUser(), requestDto);
        return ResponseEntity.ok("회원탈퇴가 완료되었습니다.");
    }
}
