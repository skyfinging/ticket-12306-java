package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class GetQueueCountEntity {
    private boolean status;
    private Integer httpstatus;
    private GetQueueCountDataEntity data;
    private List<?> messages;
}
