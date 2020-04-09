package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ConfDataEntity {
    private String name;
    private String is_login;
    private Integer other_control;
    private Integer stu_control;
}
