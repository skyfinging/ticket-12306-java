package com.drama.train.ticket.service.http.header;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * 创建Http的Header键值对，get、post请求的头部信息不一样
 */
public interface IHttpHeader {

    List<NameValuePair> getHeader();
}
