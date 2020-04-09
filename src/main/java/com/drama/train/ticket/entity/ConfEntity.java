package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConfEntity {
    private boolean status;
    private Integer httpstatus;
    private ConfDataEntity data;
    private List<?> messages;
}
