package com.project.chatfiabe.global.mail.service;

public interface MailService {

    String sendSignupMail (String email);
    void sendMail(String senderEmail, String toMail, String title, String content);

}
