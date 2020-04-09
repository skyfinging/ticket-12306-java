package com.drama.train.ticket.task;

import com.drama.train.ticket.config.BuyTicketConfig;
import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.service.HttpService;
import com.drama.train.ticket.service.MailServiceImpl;
import com.drama.train.ticket.service.StationNameService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BuyTicketTaskTest {
    @Autowired
    Http12306Config http12306Config;
    @Autowired
    HttpService httpService;
    @Autowired
    BuyTicketConfig buyTicketConfig;
    @Autowired
    MailServiceImpl mailService;
    @Autowired
    StationNameService stationNameService;

//    @Test
    public void test(){
        String uuid = "";
        BuyTicketTask buyTicketTask = new BuyTicketTask(http12306Config, buyTicketConfig, httpService, mailService, stationNameService, uuid);
        buyTicketTask.setBuyTimeBegin("17:35:00");
        buyTicketTask.setBuyTimeEnd("17:36:00");
        buyTicketTask.run();
    }
}