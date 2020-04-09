package com.drama.train.ticket.service.http;

import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.service.http.header.IHttpHeader;
import com.drama.train.ticket.service.http.header.TicketHttpHeader;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Log4j2
public class Http12306CheckUserAction extends AbstractHttpAction {

    public Http12306CheckUserAction(Http12306Config http12306Config) {
        super(http12306Config);
    }

    /**
     *
     * @param httpParameter 1个参数,不需要设置参数值
     *   _json_att=
     * @return
     */
    @Override
    public URI prepareUrl(HttpParameter httpParameter) {
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(http12306Config.getScheme())
                    .setHost(http12306Config.getHost())
                    .setPath(http12306Config.getCheckUserUrl());
            if(httpParameter ==null)
                return uriBuilder.build();
            return httpParameter.setParameterToURL(uriBuilder).build();
        } catch (URISyntaxException e) {
            log.error("无法获取url："+e.getMessage(), e);
        }
        return null;
    }

    @Override
    public IHttpHeader prepareHttpHeader() {
        return new TicketHttpHeader(http12306Config);
    }

    @Override
    public HttpResult action(HttpClientContext context, CookieStore cookieStore, ActionParameter actionParameter) {
        HttpParameter httpParameter = HttpParameter.of().addParameter("_json_att","");
        HttpResult httpResult = doRequest(context, cookieStore, httpParameter);
        //该请求好像可有可无
        httpResult.setResultResponseOK(null);
        return httpResult;
    }
}
