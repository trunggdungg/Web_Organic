package com.example.web_organic.service;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    public void sendMailResigter(String receiver, String subject, String content) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(receiver);
        mail.setSubject(subject);
        mail.setText(content);

        javaMailSender.send(mail);
        // send mail
    }
}