package com.drama.train.ticket.service.http;

import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.service.HttpService;
import com.drama.train.ticket.service.http.cookie.DeviceHttpCookie;
import com.drama.train.ticket.service.http.cookie.IHttpCookie;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import com.drama.train.ticket.service.http.request.HttpGetRequest;
import com.drama.train.ticket.service.http.request.IHttpRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Log4j2
public class Http12306KeepAliveAction extends AbstractHttpAction {

    private final HttpService httpService;

    public Http12306KeepAliveAction(Http12306Config http12306Config, HttpService httpService) {
        super(http12306Config);
        this.httpService = httpService;
    }

    /**
     *
     * @param httpParameter 无参数
     * @return
     */
    @Override
    public URI prepareUrl(HttpParameter httpParameter) {
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(http12306Config.getScheme())
                    .setHost(http12306Config.getHost())
                    .setPath(http12306Config.getKeyAliveUrl());
            if(httpParameter ==null)
                return uriBuilder.build();
            return httpParameter.setParameterToURL(uriBuilder).build();
        } catch (URISyntaxException e) {
            log.error("无法获取url："+e.getMessage(), e);
        }
        return null;
    }

    @Override
    public IHttpRequest prepareHttpRequest() {
        return new HttpGetRequest();
    }

    @Override
    public IHttpCookie prepareHttpCookie() {
        return new DeviceHttpCookie(httpService);
    }

    @Override
    public HttpResult action(HttpClientContext context, CookieStore cookieStore, ActionParameter actionParameter) {
        return doRequest(context, cookieStore,null);
    }

}
