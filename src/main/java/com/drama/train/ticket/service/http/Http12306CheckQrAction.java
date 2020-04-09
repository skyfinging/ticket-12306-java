package com.drama.train.ticket.service.http;

import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.CheckQREntity;
import com.drama.train.ticket.service.HttpService;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Log4j2
public class Http12306CheckQrAction extends AbstractHttpAction {
    public static final String NAME_PARAM_UUID = "uuid";

    private final HttpService httpService;
    public Http12306CheckQrAction(Http12306Config http12306Config, HttpService httpService) {
        super(http12306Config);
        this.httpService = httpService;
    }

    /**
     *
     * @param httpParameter 两个参数appid,uuid
     * @return
     */
    @Override
    public URI prepareUrl(HttpParameter httpParameter) {
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(http12306Config.getScheme())
                    .setHost(http12306Config.getHost())
                    .setPath(http12306Config.getCheckqrUrl());
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
                    .setResultSendFail("没有传递uuid给检查二维码状态请求");
        }
        String uuid = actionParameter.getValue(NAME_PARAM_UUID);
        if(uuid==null){
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递uuid给检查二维码状态请求");
        }
        HttpParameter httpParameter = HttpParameter.of()
                .addParameter("uuid",uuid)
                .addParameter("appid",httpService.getAppId());
        HttpResult httpResult = doRequest(context, cookieStore, httpParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.HTTP_OK) {
            log.info("无法获取二维码状态");
            return httpResult;
        }
        try {
            CheckQREntity checkQREntity = Http12306CheckQrAction.parseCheckQREntity(httpResult.getResponseText());
            if (!checkQREntity.getResult_code().equals(CheckQREntity.CODE_LOGIN)) {
                log.info("等待扫描二维码登陆");
                return httpResult.setResultInterrupt("等待扫描二维码登录");
            }
            return httpResult.setResultResponseOK(checkQREntity);
        }catch (JsonProcessingException e) {
            log.error(httpResult.getResponseText());
            log.error(e.getMessage(), e);
            httpResult.setResultSendFail("无法解析二维码状态,"+e.getMessage());
            return httpResult;
        }
    }

    public static CheckQREntity parseCheckQREntity(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        CheckQREntity checkQREntity = mapper.readValue(json, CheckQREntity.class);
        return checkQREntity;
    }
}
