package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetQueueCountDataEntity {
    private String count;
    private String ticket;
    private String op_2;
    private String countT;
    private String op_1;
    private String errMsg;
}
