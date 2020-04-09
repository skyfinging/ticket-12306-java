package com.drama.train.ticket.service.http.context;

import org.apache.http.client.protocol.HttpClientContext;

/**
 * Http上下文，除了扫码登陆需要创建一个新的上下文，其他请求直接继承上下文即可，才能保持和扫码的时候用的是同一个连接
 */
public interface IHttpContext {

    HttpClientContext creatHttpContext(HttpClientContext context);
}
