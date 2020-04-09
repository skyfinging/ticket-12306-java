package com.drama.train.ticket.service.http.parameter;

import org.apache.http.HttpEntity;

/**
 * 设置Request的参数
 */
public interface IHttpParameter {

    HttpEntity getParameter(HttpParameter httpParameter);
}
