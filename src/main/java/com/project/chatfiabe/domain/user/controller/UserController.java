package com.project.chatfiabe.domain.user.controller;

import com.project.chatfiabe.domain.user.dto.*;
import com.project.chatfiabe.domain.user.security.UserDetailsImpl;
import com.project.chatfiabe.domain.user.service.TokenBlackListService;
import com.project.chatfiabe.domain.user.service.UserService;
import com.project.chatfiabe.global.exception.BaseException;
import com.project.chatfiabe.global.exception.BaseResponse;
import com.project.chatfiabe.global.exception.BaseResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final TokenBlackListService tokenBlackListService;

    // 1. 회원가입
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<Void>> registerAccount(@Valid @RequestBody SignupRequestDto requestDto,
                                                              BindingResult bindingResult)
            throws MethodArgumentNotValidException {
        userService.registerAccount(requestDto, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse<>(BaseResponseStatus.REGISTER_ACCOUNT_SUCCESS));
    }

    // 3. 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     HttpServletResponse httpServletResponse) {
        try {
            userService.logout(userDetails.getUser(), httpServletResponse);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse<>(BaseResponseStatus.LOGOUT_SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(BaseResponseStatus.LOGOUT_FAILED));
        }
    }

//    /**
//     * 토큰 리프레시
//     * @param request HTTP 요청 객체
//     * @return 새로 발급된 액세스 토큰과 HTTP 상태 코드 200 (OK)
//     */
//    @GetMapping("/refresh")
//    public ResponseEntity<String> refresh(HttpServletRequest request) {
//        String accessToken = authService.refreshAccessToken(jwtProvider.getJwtFromHeader(request, TokenType.REFRESH));
//        return ResponseEntity.ok(accessToken);
//    }

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
    @GetMapping("/info")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            log.error("UserDetails is null");
            return ResponseEntity.status(401).build();
        }

        log.info("UserDetails: {}", userDetails);
        UserInfoResponseDto userInfoResponseDto = userService.getUserInfo(userDetails.getUser());
        return ResponseEntity.ok(userInfoResponseDto);
    }

    // 4. 회원탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse<Void>> deleteAccount(@Valid @RequestBody DeleteRequestDto deleteRequestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            HttpServletResponse httpServletResponse) {
        try {
            userService.deleteAccount(userDetails.getUser(), deleteRequestDto.getPassword(), httpServletResponse);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse<>(BaseResponseStatus.DELETE_ACCOUNT_SUCCESS));
        } catch (BaseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(BaseResponseStatus.DELETE_ACCOUNT_FAILED));
        }
    }
}
