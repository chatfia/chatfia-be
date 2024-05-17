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
@RequestMapping("/api/auth")
public class UserController {
    private final JwtProvider jwtProvider;
    private final AuthService authService;
    private final UserService userService;
    private final TokenBlackListService tokenBlackListService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        return ResponseEntity.ok(userService.signup(requestDto));
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        tokenBlackListService.addToBlackList(
                jwtProvider.getJwtFromHeader(request, TokenType.ACCESS),
                jwtProvider.getJwtFromHeader(request, TokenType.REFRESH)
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/refresh")
    public ResponseEntity<String> refresh(HttpServletRequest request) {
        String accessToken = authService.refreshAccessToken(jwtProvider.getJwtFromHeader(request, TokenType.REFRESH));
        return ResponseEntity.ok(accessToken);
    }

    @GetMapping("/blacklist/reset")
    public ResponseEntity<Void> resetBlackList() {
        tokenBlackListService.removeExpiredTokens();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 닉네임 수정
    @PatchMapping("/updateNickname")
    public ResponseEntity<UserInfoResponseDto> updateNickname(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserInfoRequestDto requestDto) {
        UserInfoResponseDto updatedUser = userService.updateUserInfo(userDetails.getUser(), requestDto.getNickname());
        return ResponseEntity.ok(updatedUser);
    }

    // 비밀번호 변경
    // 현재 비밀번호, 새 비밀번호 == 새 비밀번호 확인
    @PatchMapping("/updatePassword")
    public ResponseEntity<String> updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserInfoRequestDto requestDto) {
        userService.updatePassword(userDetails.getUser(), requestDto);
        return ResponseEntity.ok("비밀번호 수정이 완료되었습니다.");
    }

    // 회원 탈퇴
    @DeleteMapping
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody DeleteUserInfoRequestDto requestDto) {
        userService.deleteUser(userDetails.getUser(), requestDto);
        return ResponseEntity.ok("회원탈퇴가 완료되었습니다.");
    }
}
