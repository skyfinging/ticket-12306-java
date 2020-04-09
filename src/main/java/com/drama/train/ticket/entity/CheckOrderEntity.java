package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CheckOrderEntity {
    private boolean status;
    private Integer httpstatus;
    private CheckOrderDataEntity data;
    private List<?> messages;
}


