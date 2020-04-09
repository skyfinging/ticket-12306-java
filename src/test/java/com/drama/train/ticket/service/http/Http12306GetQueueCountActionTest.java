package com.drama.train.ticket.service.http;

import org.junit.Test;

import static org.junit.Assert.*;

public class Http12306GetQueueCountActionTest {

    @Test
    public void getTrainDate() {
        String date = "2020-02-04";
        String train_time = Http12306GetQueueCountAction.getTrainDate(date);
        System.out.println(train_time);
        assertEquals("Tue Feb 04 2020 00:00:00 GMT+0800 (中国标准时间)", train_time);
    }
}