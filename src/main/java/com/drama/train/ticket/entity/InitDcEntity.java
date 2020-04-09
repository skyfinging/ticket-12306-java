package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InitDcEntity {
    private String globalRepeatSubmitToken;
    private String leftTicket;
    private String purposeCodes;
    private String trainLocation;
    private String keyCheckIsChange;
}
