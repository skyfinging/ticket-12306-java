package com.drama.train.ticket.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailServiceImplTest {

    @Autowired
    MailServiceImpl mailService;

//    @Test
    public void test(){
        mailService.sendSimpleMail("250805603@qq.com","购票通知","请及时付款");
    }

}