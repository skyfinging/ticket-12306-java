package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CheckQREntity {
    public static final Integer CODE_VALID_OK = 0;      //二维码有效
    public static final Integer CODE_SCANNING = 1;      //二维码正在扫描
    public static final Integer CODE_LOGIN = 2;         //二维码授权登陆
    public static final Integer CODE_TIME_OUT = 3;      //二维码失效,12306验证码失效时长为5分钟

    private String result_message;
    private Integer result_code;
    private String uamtk;
}
