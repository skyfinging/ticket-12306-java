package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SubmitOrderEntity {
    private String validateMessagesShowId;
    private boolean status;
    private Integer httpstatus;
    private String data;
    private List<Object> messages;
    private Object validateMessages;
}
