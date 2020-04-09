package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PassengerEntity{
    private String passenger_name;
    private String passenger_id_no;
    private String mobile_no;
    private String allEncStr;
}
