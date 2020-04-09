package com.drama.train.ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * 发送邮件服务
 */
@Component
public class MailServiceImpl  {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${is.send.mail}")
    private boolean isSendMail;

    @Value("${spring.mail.username}")
    private String from;

    public void sendSimpleMail(String to, String subject, String content) throws MailException {
        if(isSendMail) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from); // 邮件发送者
            message.setTo(to); // 邮件接受者
            message.setSubject(subject); // 主题
            message.setText(content); // 内容

            mailSender.send(message);
        }
    }
}