package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CheckOrderDataEntity{
    private String canChooseBeds;
    private String canChooseSeats;
    private String choose_Seats;
    private String isCanChooseMid;
    private String ifShowPassCodeTime;
    private boolean submitStatus;
    private String smokeStr;
    private String errMsg;
}
