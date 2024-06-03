package com.project.chatfiabe.global.mail.controller;

import com.project.chatfiabe.global.mail.dto.MailRequestDto;
import com.project.chatfiabe.global.mail.dto.MailResponseDto;
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
    @PostMapping
    public ResponseEntity<Void> sendSignupMail(@RequestBody MailRequestDto requestDto) {
        String code = mailService.sendSignupMail(requestDto.getEmail());
        redisService.setCode(requestDto.getEmail(), code);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 인증번호 확인 버튼 클릭
    @GetMapping
    public ResponseEntity<MailResponseDto> getCode (@RequestBody MailRequestDto requestDto) {
        String code = redisService.getCode(requestDto.getEmail());
        return new ResponseEntity<>(new MailResponseDto(code), HttpStatus.OK);
    }

}
