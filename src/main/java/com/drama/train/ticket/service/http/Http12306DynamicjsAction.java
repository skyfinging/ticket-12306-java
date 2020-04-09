package com.drama.train.ticket.service.http;

import com.drama.train.ticket.config.Http12306Config;
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
public class Http12306DynamicjsAction extends AbstractHttpAction {

    public Http12306DynamicjsAction(Http12306Config http12306Config) {
        super(http12306Config);
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
                    .setPath(http12306Config.getDynamicJsUrl());
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
    public HttpResult action(HttpClientContext context, CookieStore cookieStore, ActionParameter actionParameter) {
        HttpResult httpResult = doRequest(context, cookieStore,null);
        log.info(httpResult.getResponseText());
        return httpResult;
    }

}
