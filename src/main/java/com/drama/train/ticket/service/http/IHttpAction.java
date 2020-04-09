package com.drama.train.ticket.service.http;

import com.drama.train.ticket.service.http.context.IHttpContext;
import com.drama.train.ticket.service.http.cookie.IHttpCookie;
import com.drama.train.ticket.service.http.header.IHttpHeader;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import com.drama.train.ticket.service.http.parameter.IHttpParameter;
import com.drama.train.ticket.service.http.request.IHttpRequest;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;

import java.net.URI;

/**
 * 所有HTTP请求实现类的顶级接口
 * 发起请求之前，需要完成
 * 1.url绑定
 * 2.http头部信息绑定
 * 3.request请求参数绑定
 * 4.cookie绑定
 * 5.context绑定
 * 6.指定请求是Post还是Get
 *
 * 最后调用action发起请求
 */
public interface IHttpAction {

    URI prepareUrl(HttpParameter httpParameter);
    IHttpRequest prepareHttpRequest();
    IHttpHeader prepareHttpHeader();
    IHttpParameter prepareHttpParameter();
    IHttpCookie prepareHttpCookie();
    IHttpContext prepareHttpContext();

    HttpResult action(HttpClientContext context, CookieStore cookieStore, ActionParameter actionParameter);
}
