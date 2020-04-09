package com.drama.train.ticket.service.http;

import com.drama.train.ticket.bean.TrainInfo;
import com.drama.train.ticket.config.BuyTicketConfig;
import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.GetQueueCountEntity;
import com.drama.train.ticket.entity.InitDcEntity;
import com.drama.train.ticket.service.HttpService;
import com.drama.train.ticket.service.http.header.IHttpHeader;
import com.drama.train.ticket.service.http.header.OrderHttpHeader;
import com.drama.train.ticket.service.http.parameter.HttpPostParameter;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import com.drama.train.ticket.service.http.parameter.IHttpParameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Log4j2
public class Http12306GetQueueCountAction extends AbstractHttpAction {
    public static final String NAME_PARAM_TRAININFO = "trainInfo";
    public final static String NAME_PARAM_INITDC = "initDc";
    private final BuyTicketConfig config;

    public Http12306GetQueueCountAction(Http12306Config http12306Config, BuyTicketConfig config) {
        super(http12306Config);
        this.config = config;
    }

    /**
     *
     * @param httpParameter 11个参数
     * train_date: Tue Feb 04 2020 00:00:00 GMT+0800 (中国标准时间)
     * train_no: 65000D750110
     * stationTrainCode: D7501
     * seatType: O
     * fromStationTelecode: GGQ
     * toStationTelecode: CBQ
     * leftTicket: u4Z7dYp94qp9xjk4kxR9SZn7y8IGybbPAx9Y3ivLQLVQ7NaU
     * purpose_codes: 00
     * train_location: Q6
     * _json_att:
     * REPEAT_SUBMIT_TOKEN: bcce93aca27ffb2d36ab650ec503e1dd
     * @return
     */
    @Override
    public URI prepareUrl(HttpParameter httpParameter) {
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(http12306Config.getScheme())
                    .setHost(http12306Config.getHost())
                    .setPath(http12306Config.getQueueCountUrl());
                return uriBuilder.build();
        } catch (URISyntaxException e) {
            log.error("无法获取url："+e.getMessage(), e);
        }
        return null;
    }

    public static GetQueueCountEntity parseGetQueueCountEntity(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        GetQueueCountEntity entity = null;
        entity = mapper.readValue(json, GetQueueCountEntity.class);
        return entity;
    }

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("eee MMM dd yyyy HH:mm:ss", Locale.US);
    public static String getTrainDate(String yyyy_MM_dd){
        LocalDate localDate = LocalDate.parse(yyyy_MM_dd,dtf);
        LocalDateTime localDateTime = localDate.atTime(0,0,0);
        return localDateTime.format(dtf2)+" GMT+0800 (中国标准时间)";
    }

    @Override
    public IHttpParameter prepareHttpParameter() {
        return new HttpPostParameter();
    }

    @Override
    public HttpResult action(HttpClientContext context, CookieStore cookieStore, ActionParameter actionParameter) {
        if(actionParameter==null){
            log.info("没有传递参数给排队请求");
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递参数给排队请求");
        }
        TrainInfo trainInfo = actionParameter.getValue(NAME_PARAM_TRAININFO);
        if(trainInfo==null){
            log.info("没有传递车次信息给排队请求");
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递车次信息给排队请求");
        }
        InitDcEntity initDcEntity = actionParameter.getValue(NAME_PARAM_INITDC);
        if(initDcEntity==null){
            log.info("没有传递单程票信息给排队请求");
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递单程票信息给排队请求");
        }
        HttpParameter httpParameter = HttpParameter.of()
                .addParameter("train_date",Http12306GetQueueCountAction.getTrainDate(config.getTrainDate()))
                .addParameter("train_no",trainInfo.getTrainNo())
                .addParameter("stationTrainCode",trainInfo.getTrainCode())
                .addParameter("seatType", HttpService.getSeatTypeCode(config.getSeatType()))
                .addParameter("fromStationTelecode",config.getFromStationCode())
                .addParameter("toStationTelecode",config.getToStationCode())
                .addParameter("leftTicket",initDcEntity.getLeftTicket())
                .addParameter("purpose_codes",initDcEntity.getPurposeCodes())
                .addParameter("train_location",initDcEntity.getTrainLocation())
                .addParameter("_json_att","")
                .addParameter("REPEAT_SUBMIT_TOKEN",initDcEntity.getGlobalRepeatSubmitToken());
        HttpResult httpResult = doRequest(context, cookieStore, httpParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.HTTP_OK){
            log.info("无法获取购票排队信息");
            return httpResult;
        }
        try{
            GetQueueCountEntity getQueueCountEntity = Http12306GetQueueCountAction.parseGetQueueCountEntity(httpResult.getResponseText());
            log.info(trainInfo.getTrainCode()+"排队信息："+getQueueCountEntity.getData());
            httpResult.setResultResponseOK(getQueueCountEntity);
            return httpResult;
        } catch (JsonProcessingException e) {
            log.error(httpResult.getResponseText());
            log.error(e.getMessage(), e);
            httpResult.setResultSendFail("无法解析购票排队信息:"+e.getMessage());
            return httpResult;
        }
    }

    @Override
    public IHttpHeader prepareHttpHeader() {
        return new OrderHttpHeader(http12306Config);
    }
}
