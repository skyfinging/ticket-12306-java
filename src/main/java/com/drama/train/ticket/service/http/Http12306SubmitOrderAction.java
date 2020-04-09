package com.drama.train.ticket.service.http;

import com.drama.train.ticket.bean.TrainInfo;
import com.drama.train.ticket.config.BuyTicketConfig;
import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.SubmitOrderEntity;
import com.drama.train.ticket.service.http.header.IHttpHeader;
import com.drama.train.ticket.service.http.header.TicketHttpHeader;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

@Log4j2
public class Http12306SubmitOrderAction extends AbstractHttpAction {
    public final static String NAME_PARAM_TRAININFO = "trainInfo";

    final private BuyTicketConfig config;

    public Http12306SubmitOrderAction(Http12306Config http12306Config, BuyTicketConfig config) {
        super(http12306Config);
        this.config = config;
    }

    /**
     *
     * @param httpParameter 8个参数
     * secretStr: HYJHIUpfhb3Pgg0UczrHGmqSF9yfnJJUE2dOdeMJk+uwOHuJ4m/aag7Zon+KEIDjal28AgOCPLxD4oSpTI6LdOVwYERRZN1qZrMoKaprVFQtxp7ozll9wlSMq1vKPS3haa0ChDptGjexlhjKAMsu2nJHf6dHFa5gOBHje5F4b64aJyTvNRIWDIwbbQOPje3wJx3c+h5vZewVKYToTbIfd6bOXI9RDRstCsJ1thFIfIH/JeipYhcZE7EhccmWMLwwXmCzmd5cTn/HcR8rTXQfJu9GxP/q8djYgskdYbOt3ck=
     * train_date: 2020-01-05
     * back_train_date: 2020-01-05
     * tour_flag: dc
     * purpose_codes: ADULT
     * query_from_station_name: 广州
     * query_to_station_name: 潮汕
     * undefined:
     * @return
     */
    @Override
    public URI prepareUrl(HttpParameter httpParameter) {
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(http12306Config.getScheme())
                    .setHost(http12306Config.getHost())
                    .setPath(http12306Config.getSubmitOrderUrl());
            if(httpParameter ==null)
                return uriBuilder.build();
            return httpParameter.setParameterToURL(uriBuilder).build();
        } catch (URISyntaxException e) {
            log.error("无法获取url："+e.getMessage(), e);
        }
        return null;
    }

    public static SubmitOrderEntity parseSubmitOrderEntity(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(json, SubmitOrderEntity.class);
    }

    @Override
    public IHttpHeader prepareHttpHeader() {
        return new TicketHttpHeader(http12306Config);
    }

    @Override
    public HttpResult action(HttpClientContext context, CookieStore cookieStore, ActionParameter actionParameter) {
        //检查参数
        if(actionParameter==null){
            log.info("没有传递车次信息给预订订单");
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递车次信息给预订订单");
        }
        //检查车次信息
        TrainInfo trainInfo = actionParameter.getValue(NAME_PARAM_TRAININFO);
        if(trainInfo==null){
            log.info("没有传递车次信息给预订订单");
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递车次信息给预订订单");
        }
        //转换secretStr参数
        String secretStr = null;
        try {
            secretStr = URLDecoder.decode(trainInfo.getSecretStr(), "UTF-8");
        }catch (UnsupportedEncodingException e) {
            log.info("对参数secretStr的值进行编码错误："+trainInfo.getSecretStr());
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("对参数secretStr的值进行编码错误："+trainInfo.getSecretStr());
        }

        HttpParameter httpParameter = HttpParameter.of()
                    .addParameter("secretStr", secretStr)
                    .addParameter("train_date",config.getTrainDate())
                    .addParameter("back_train_date",config.getTrainDate())
                    .addParameter("tour_flag",config.getTourFlag())
                    .addParameter("purpose_codes",config.getPurposeCodes())
                    .addParameter("query_from_station_name",config.getFromStation())
                    .addParameter("query_to_station_name",config.getFromStation())
                    .addParameter("undefined","");
        HttpResult httpResult = doRequest(context, cookieStore, httpParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.HTTP_OK){
            log.info("无法提交预订");
            return httpResult;
        }
        try {
            SubmitOrderEntity submitOrderEntity = parseSubmitOrderEntity(httpResult.getResponseText());
            if(!submitOrderEntity.isStatus()){
                String msg = "预订失败"+":"+httpResult.getResponseText();
                log.warn(msg);
                httpResult.setResultResponseError(msg);
                return httpResult;
            }else{
                httpResult.setResultResponseOK(submitOrderEntity);
            }
            return httpResult;
        } catch (JsonProcessingException e) {
            log.error(httpResult.getResponseText());
            log.error(e.getMessage(), e);
            httpResult.setResultSendFail("无法解析提交预定的结果:"+e.getMessage());
            return httpResult;
        }
    }
}
