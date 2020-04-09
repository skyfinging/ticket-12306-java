package com.drama.train.ticket.service.http;

import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.service.HttpService;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Log4j2
public class Http12306UamtkAction extends AbstractHttpAction {
    private final HttpService httpService;

    public Http12306UamtkAction(Http12306Config http12306Config, HttpService httpService) {
        super(http12306Config);
        this.httpService = httpService;
    }

    /**
     *
     * @param httpParameter 1个参数appid
     * @return
     */
    @Override
    public URI prepareUrl(HttpParameter httpParameter) {
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(http12306Config.getScheme())
                    .setHost(http12306Config.getHost())
                    .setPath(http12306Config.getUamtkUrl());
            if(httpParameter ==null)
                return uriBuilder.build();
            return httpParameter.setParameterToURL(uriBuilder).build();
        } catch (URISyntaxException e) {
            log.error("无法获取url："+e.getMessage(), e);
        }
        return null;
    }

    @Override
    public HttpResult action(HttpClientContext context, CookieStore cookieStore, ActionParameter actionParameter) {
        HttpParameter httpParameter = HttpParameter.of().addParameter("appid",httpService.getAppId());
        return doRequest(context, cookieStore, httpParameter);
    }
}
