package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QRCodeEntity {
    private String image;
    private String result_message;
    private Integer result_code;
    private String uuid;
}
