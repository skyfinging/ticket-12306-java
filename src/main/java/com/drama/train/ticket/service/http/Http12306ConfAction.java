package com.drama.train.ticket.service.http;

import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.ConfEntity;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Log4j2
public class Http12306ConfAction extends AbstractHttpAction {

    public Http12306ConfAction(Http12306Config http12306Config) {
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
                    .setPath(http12306Config.getConfUrl());
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
        HttpResult httpResult = doRequest(context ,cookieStore, null);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.HTTP_OK) {
            log.info("获取登陆用户信息失败");
            return httpResult;
        }
        if(actionParameter!=null) {
            try {
                ConfEntity confEntity = Http12306ConfAction.parseConfEntity(httpResult.getResponseText());
                if (!confEntity.isStatus() || !"Y".equalsIgnoreCase(confEntity.getData().getIs_login())) {
                    log.info("登陆失败，"+httpResult.getResponseText());
                    httpResult.setResultResponseError("登录失败");
                    return httpResult;
                }
                httpResult.setResultResponseOK(confEntity);
                return httpResult;
            } catch (JsonProcessingException e) {
                log.error(httpResult.getResponseText());
                log.error(e.getMessage(), e);
                httpResult.setResultSendFail("无法解析登录用户信息,"+e.getMessage());
                return httpResult;
            }
        }
        return httpResult;
    }

    public static ConfEntity parseConfEntity(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ConfEntity entity = null;
        entity = mapper.readValue(json, ConfEntity.class);
        return entity;
    }

}
