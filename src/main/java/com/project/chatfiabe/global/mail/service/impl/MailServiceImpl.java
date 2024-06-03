package com.project.chatfiabe.global.mail.service.impl;

import com.project.chatfiabe.global.mail.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;
    private static final String EMAIL_TITLE_PREFIX = "[CHATFIA] ";
    private static final String SIGNUP_EMAIL_CONTENT_TEMPLATE = "아래의 인증번호를 입력하여 회원가입을 완료해주세요."+
            "<br><br>" +
            "인증번호 %s";

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    @Transactional
    public String sendSignupMail (String email) {
        // 1000에서 9999 사이 랜덤 숫자 생성
        Random random = new Random();
        String code = String.valueOf(random.nextInt(9000) + 1000);

        String content = String.format(SIGNUP_EMAIL_CONTENT_TEMPLATE, code);
        sendMail(senderEmail, email, EMAIL_TITLE_PREFIX, content);

        return code;
    }

    @Override
    @Transactional
    public void sendMail(String senderEmail, String toMail, String title, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(senderEmail);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
