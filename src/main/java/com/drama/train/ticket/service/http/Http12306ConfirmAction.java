package com.drama.train.ticket.service.http;

import com.drama.train.ticket.config.BuyTicketConfig;
import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.ConfirmEntity;
import com.drama.train.ticket.entity.InitDcEntity;
import com.drama.train.ticket.entity.PassengerEntity;
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

@Log4j2
public class Http12306ConfirmAction extends AbstractHttpAction {
    public final static String NAME_PARAM_INITDC = "initDc";
    public final static String NAME_PARAM_PASSENGER = "passenger";

    private final BuyTicketConfig config;
    public Http12306ConfirmAction(Http12306Config http12306Config, BuyTicketConfig config) {
        super(http12306Config);
        this.config = config;
    }

    /**
     *
     * @param httpParameter 14个参数
     * passengerTicketStr: O,0,1,陈锐均,1,4451***********650,18664844539,N,216c20718a316cc5de0ed7027a18b9151425eab660a56e8024888b4773a7f3ab8cb5c6a6e3ccb6de39b8ce423fe63948
     * oldPassengerStr: 陈锐均,1,4451***********650,1_
     * randCode:
     * purpose_codes: 00
     * key_check_isChange: 5F338583F74544C91487226CF23E5EBF8C95F090DBF2272309ACFD03
     * leftTicketStr: Oh5UDLk%2FcZhK6Hmer%2FTShIF%2Fa4ynNj6OpYvz945HSRtM25P4
     * train_location: Q6
     * choose_seats:
     * seatDetailType: 000
     * whatsSelect: 1
     * roomType: 00
     * dwAll: N
     * _json_att:
     * REPEAT_SUBMIT_TOKEN: 665a3706dae30575372d155bfc7af543
     * @return
     */
    @Override
    public URI prepareUrl(HttpParameter httpParameter) {
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(http12306Config.getScheme())
                    .setHost(http12306Config.getHost())
                    .setPath(http12306Config.getConfirmUrl());
                return uriBuilder.build();
        } catch (URISyntaxException e) {
            log.error("无法获取url："+e.getMessage(), e);
        }
        return null;
    }

    public static ConfirmEntity parseConfirmEntity(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ConfirmEntity entity = null;
        entity = mapper.readValue(json, ConfirmEntity.class);
        return entity;
    }

    @Override
    public IHttpParameter prepareHttpParameter() {
        return new HttpPostParameter();
    }

    @Override
    public HttpResult action(HttpClientContext context, CookieStore cookieStore, ActionParameter actionParameter) {
        if(actionParameter==null){
            log.info("没有传递参数给提交订单请求");
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递参数给提交订单请求");
        }
        PassengerEntity passengerEntity = actionParameter.getValue(NAME_PARAM_PASSENGER);
        InitDcEntity initDcEntity = actionParameter.getValue(NAME_PARAM_INITDC);
        if(passengerEntity==null){
            log.info("没有传递乘客信息给提交订单请求");
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递乘客信息给提交订单请求");
        }
        if(initDcEntity==null){
            log.info("没有传递单程票信息给提交订单请求");
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递单程票信息给提交订单请求");
        }
        HttpParameter httpParameter = HttpParameter.of()
                .addParameter("passengerTicketStr",Http12306CheckOrderAction.getPassengerTicketStr(passengerEntity, config.getSeatType()))
                .addParameter("oldPassengerStr", Http12306CheckOrderAction.getOldPassengerStr(passengerEntity))
                .addParameter("randCode","")
                .addParameter("purpose_codes",initDcEntity.getPurposeCodes())
                .addParameter("key_check_isChange",initDcEntity.getKeyCheckIsChange())
                .addParameter("leftTicketStr",initDcEntity.getLeftTicket())
                .addParameter("train_location",initDcEntity.getTrainLocation())
                .addParameter("choose_seats",config.getChooseSeat())
                .addParameter("seatDetailType","000")   //不知道这个值是不是固定的
                .addParameter("whatsSelect","1")
                .addParameter("roomType","00")
                .addParameter("dwAll","N")
                .addParameter("_json_att","")
                .addParameter("REPEAT_SUBMIT_TOKEN",initDcEntity.getGlobalRepeatSubmitToken());
        HttpResult httpResult = doRequest(context, cookieStore, httpParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.HTTP_OK){
            log.info("无法提交订单");
            return httpResult;
        }
        try {
            ConfirmEntity confirmEntity = parseConfirmEntity(httpResult.getResponseText());
            if(confirmEntity.isStatus()==false || confirmEntity.getData().isSubmitStatus()==false){
                String msg ="出票失败，"+httpResult.getResponseText();
                log.info(msg);
                httpResult.setResultResponseError(msg);
                return httpResult;
            }else
                httpResult.setResultResponseOK(confirmEntity);
            return httpResult;
        } catch (JsonProcessingException e) {
            log.error(httpResult.getResponseText());
            log.error(e.getMessage(), e);
            httpResult.setResultSendFail("无法解析提交订单信息:"+e.getMessage());
            return httpResult;
        }
    }

    @Override
    public IHttpHeader prepareHttpHeader() {
        return new OrderHttpHeader(http12306Config);
    }
}
