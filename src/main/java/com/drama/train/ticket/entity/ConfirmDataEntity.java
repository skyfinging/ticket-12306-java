package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ConfirmDataEntity {
    private boolean submitStatus;
    private String errMsg;
}
