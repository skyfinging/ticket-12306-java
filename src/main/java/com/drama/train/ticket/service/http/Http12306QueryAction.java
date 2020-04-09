package com.drama.train.ticket.service.http;

import com.drama.train.ticket.config.BuyTicketConfig;
import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.QueryZEntity;
import com.drama.train.ticket.service.HttpService;
import com.drama.train.ticket.service.http.cookie.IHttpCookie;
import com.drama.train.ticket.service.http.cookie.TicketHttpCookie;
import com.drama.train.ticket.service.http.header.IHttpHeader;
import com.drama.train.ticket.service.http.header.TicketHttpHeader;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import com.drama.train.ticket.service.http.request.HttpGetRequest;
import com.drama.train.ticket.service.http.request.IHttpRequest;
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
public class Http12306QueryAction extends AbstractHttpAction {
    private final HttpService httpService;
    private final BuyTicketConfig buyTicketConfig;

    public Http12306QueryAction(Http12306Config http12306Config, HttpService httpService, BuyTicketConfig buyTicketConfig) {
        super(http12306Config);
        this.httpService = httpService;
        this.buyTicketConfig = buyTicketConfig;
    }

    /**
     *
     * @param httpParameter 四个参数
     * leftTicketDTO.train_date: 2020-01-05
     * leftTicketDTO.from_station: GGQ
     * leftTicketDTO.to_station: CBQ
     * purpose_codes: ADULT
     * @return
     */
    @Override
    public URI prepareUrl(HttpParameter httpParameter) {
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(http12306Config.getScheme())
                    .setHost(http12306Config.getHost())
                    .setPath(http12306Config.getQueryUrl());
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
        HttpParameter httpParameter = HttpParameter.of()
                .addParameter("leftTicketDTO.train_date",buyTicketConfig.getTrainDate())
                .addParameter("leftTicketDTO.from_station",buyTicketConfig.getFromStationCode())
                .addParameter("leftTicketDTO.to_station",buyTicketConfig.getToStationCode())
                .addParameter("purpose_codes",buyTicketConfig.getPurposeCodes());
        if(buyTicketConfig.getFromStationCode()==null || buyTicketConfig.getToStationCode()==null) {
            String msg = "无法获取车站代码，请检查配置文件中的车站名称是否正确";
            log.error(msg);
            return new HttpResult(null, context, cookieStore, null,"")
                    .setResultSendFail(msg);
        }

        HttpResult httpResult = doRequest(context, cookieStore, httpParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.HTTP_OK){
            log.info("查询车票失败");
            return httpResult;
        }

        try {
            QueryZEntity queryZEntity = parseQueryZEntity(httpResult.getResponseText());
            if(!queryZEntity.isStatus()){
                log.info("查询车票失败:"+httpResult.getResponseText());
                httpResult.setResultResponseError("查询车票失败:"+httpResult.getResponseText());
            }else{
                httpResult.setResultResponseOK(queryZEntity);
            }
            return httpResult;
        }catch (JsonProcessingException e) {
            log.error(httpResult.getResponseText());
            log.error(e.getMessage(), e);
            httpResult.setResultSendFail("无法解析车票查询结果:"+e.getMessage());
            return httpResult;
        }
    }

    public static QueryZEntity parseQueryZEntity(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(json, QueryZEntity.class);
    }

    @Override
    public IHttpRequest prepareHttpRequest() {
        return new HttpGetRequest();
    }

    @Override
    public IHttpHeader prepareHttpHeader() {
        return new TicketHttpHeader(http12306Config);
    }

    @Override
    public IHttpCookie prepareHttpCookie() {
        return new TicketHttpCookie(httpService, buyTicketConfig);
    }
}
