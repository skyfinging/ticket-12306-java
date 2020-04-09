package com.drama.train.ticket.service.http;

public enum HttpResultStatusEnum{
    /**
     * http发送之前就已经发生错误，这种情况业务不能重试发送，因为还会继续发生相同的错误
     */
    HTTP_SEND_FAIL,
    /**
     * http发送成功，但是http请求返回的状态码不是200
     */
    HTTP_FAIL,
    /**
     * http发送成功，并且http状态码是200
     */
    HTTP_OK,
    /**
     * http发送成功，但是服务器返回错误，这时候http的状态码是200
     */
    RESPONSE_ERROR,
    /**
     * http发送成功，并且服务器返回成功的结果
     */
    RESPONSE_OK,
    /**
     * 有时候，http通信没有问题，但是因为业务需求，导致流程不能继续下去，流程需要提前中断返回
     * 中断之后，可以继续重试业务
     */
    INTERRUPT
    ;
}