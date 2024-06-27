package com.project.chatfiabe.global.mail.controller;

import com.project.chatfiabe.global.mail.dto.MailRequestDto;
import com.project.chatfiabe.global.mail.service.MailService;
import com.project.chatfiabe.global.mail.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;
    private final RedisService redisService;

    // 이메일 인증 버튼 클릭
//    @PostMapping
//    public ResponseEntity<Void> sendSignupMail(@RequestBody MailRequestDto requestDto) {
//        String code = mailService.sendSignupMail(requestDto.getEmail());
//        redisService.setCode(requestDto.getEmail(), code);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    // 테스트용 이메일 인증
    @PostMapping
    public ResponseEntity<String> sendSignupMail(@RequestBody MailRequestDto requestDto) {
        try {
            String code = mailService.sendSignupMail(requestDto.getEmail());
            redisService.setCode(requestDto.getEmail(), code);
            return new ResponseEntity<>("성공", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Redis connection failed: " + e.getMessage());
        }
    }

    @PostMapping("/code")
    public ResponseEntity<Void> getCode (@RequestBody MailRequestDto requestDto) {
        String code = redisService.getCode(requestDto.getEmail());
        if (!code.equals(requestDto.getCode())) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
