package com.drama.train.ticket.service.http;

import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Log4j2
public class Http12306ClientAction extends AbstractHttpAction {
    public final static String NAME_PARAM_TK = "tk";
    public Http12306ClientAction(Http12306Config http12306Config) {
        super(http12306Config);
    }

    /**
     *
     * @param httpParameter 1个参数tk
     * @return
     */
    @Override
    public URI prepareUrl(HttpParameter httpParameter) {
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(http12306Config.getScheme())
                    .setHost(http12306Config.getHost())
                    .setPath(http12306Config.getUamuthClientUrl());
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
        if(actionParameter==null){
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递tk给client请求");
        }
        String tk = actionParameter.getValue(NAME_PARAM_TK);
        if(tk==null){
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递tk给client请求");
        }
        HttpParameter httpParameter = HttpParameter.of().addParameter("tk",tk);
        HttpResult httpResult = doRequest(context, cookieStore, httpParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.HTTP_OK){
            log.info("client信息请求失败");
            return httpResult;
        }
        httpResult.setResultResponseOK(null);
        return httpResult;
    }
}
