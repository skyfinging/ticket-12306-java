package com.drama.train.ticket.service.http;

import com.drama.train.ticket.config.BuyTicketConfig;
import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.InitDcEntity;
import com.drama.train.ticket.entity.PassengerDtoEntity;
import com.drama.train.ticket.entity.PassengerEntity;
import com.drama.train.ticket.service.http.header.IHttpHeader;
import com.drama.train.ticket.service.http.header.OrderHttpHeader;
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
public class Http12306PassengerDtoAction extends AbstractHttpAction {
    public final static String NAME_PARAM_INITDC = "initDc";

    private final BuyTicketConfig config;

    public Http12306PassengerDtoAction(Http12306Config http12306Config, BuyTicketConfig config) {
        super(http12306Config);
        this.config = config;
    }

    /**
     *
     * @param httpParameter 2个参数
     * _json_att:
     * REPEAT_SUBMIT_TOKEN: ff36a258129ebd084319479b7591bd64
     * @return
     */
    @Override
    public URI prepareUrl(HttpParameter httpParameter) {
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(http12306Config.getScheme())
                    .setHost(http12306Config.getHost())
                    .setPath(http12306Config.getPassengerDtoUrl());
            if(httpParameter ==null)
                return uriBuilder.build();
            return httpParameter.setParameterToURL(uriBuilder).build();
        } catch (URISyntaxException e) {
            log.error("无法获取url："+e.getMessage(), e);
        }
        return null;
    }

    public static PassengerDtoEntity parsePassengerDtoEntity(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PassengerDtoEntity entity = null;
        entity = mapper.readValue(json, PassengerDtoEntity.class);
        return entity;
    }

    @Override
    public IHttpHeader prepareHttpHeader() {
        return new OrderHttpHeader(http12306Config);
    }

    @Override
    public HttpResult action(HttpClientContext context, CookieStore cookieStore, ActionParameter actionParameter) {
        if(actionParameter==null){
            log.info("没有传递单程票信息给用户信息获取请求");
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递单程票信息给用户信息获取请求");
        }
        InitDcEntity initDcEntity = actionParameter.getValue(NAME_PARAM_INITDC);
        if(initDcEntity==null){
            log.info("没有传递单程票信息给用户信息获取请求");
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递单程票信息给用户信息获取请求");
        }
        HttpParameter httpParameter = HttpParameter.of()
                .addParameter("_json_att","")
                .addParameter("REPEAT_SUBMIT_TOKEN",initDcEntity.getGlobalRepeatSubmitToken());
        HttpResult httpResult = doRequest(context, cookieStore, httpParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.HTTP_OK){
            log.info("无法获取乘客信息");
            return httpResult;
        }
        try {
            PassengerDtoEntity entity = parsePassengerDtoEntity(httpResult.getResponseText());
            if(!entity.isStatus()){
                String msg = "获取乘客信息失败"+":"+httpResult.getResponseText();
                log.info(msg);
                httpResult.setResultResponseError(msg);
                return httpResult;
            }
            PassengerEntity passenger = entity.getPassenger(config.getPassenger());
            if(passenger==null){
                String msg = "无法匹配到乘客信息,"+httpResult.getResponseText();
                log.info(msg);
                httpResult.setResultSendFail(msg);
                return httpResult;
            }else
                httpResult.setResultResponseOK(passenger);
            return httpResult;
        } catch (JsonProcessingException e) {
            log.error(httpResult.getResponseText());
            log.error(e.getMessage(), e);
            httpResult.setResultSendFail("无法解析用户信息:"+e.getMessage());
            return httpResult;
        }
    }
}
