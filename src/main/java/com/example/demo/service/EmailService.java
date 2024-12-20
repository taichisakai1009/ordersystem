package com.example.demo.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    
    private final TemplateEngine templateEngine;
    
    EmailService (JavaMailSender mailSender, TemplateEngine templateEngine) {
    	this.mailSender = mailSender;
    	this.templateEngine =  templateEngine;
    }

    @Async
    public void sendSimpleMessage(String to, String subject, String temlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(temlContent, true); // HTMLを送信するときは第二引数を追加して true を代入
        mailSender.send(message);
        System.out.println("メール送信終了");
    }
    
    @Async
    public void sendFileMessage(String to, String subject, String temlContent, String fileName, byte[] bytes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(temlContent, true); // HTMLを送信するときは第二引数を追加して true を代入
        helper.addAttachment(fileName, new ByteArrayResource(bytes)); // "attachment"は「添付」の意味
        mailSender.send(message);
        System.out.println("メール送信終了");
    }
    
}
