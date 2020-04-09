package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ConfirmEntity {
    private boolean status;
    private Integer httpstatus;
    private ConfirmDataEntity data;
    private List<?> messages;
}
