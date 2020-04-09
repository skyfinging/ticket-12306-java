package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthUamtkEntity {
    private String result_message;
    private Integer result_code;
    private String apptk;
    private String newapptk;
}
