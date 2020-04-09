package com.drama.train.ticket.service.http.cookie;


import org.apache.http.client.CookieStore;

/**
 * Http的cookie设置接口
 * 有的请求需要新增cookie设置，继承该接口
 */
public interface IHttpCookie {
    CookieStore createCookieStore(CookieStore cookieStore);
}
