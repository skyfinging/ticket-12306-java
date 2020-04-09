package com.drama.train.ticket.service.http;

import com.drama.train.ticket.config.BuyTicketConfig;
import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.CheckOrderEntity;
import com.drama.train.ticket.entity.InitDcEntity;
import com.drama.train.ticket.entity.PassengerEntity;
import com.drama.train.ticket.service.HttpService;
import com.drama.train.ticket.service.http.header.IHttpHeader;
import com.drama.train.ticket.service.http.header.OrderHttpHeader;
import com.drama.train.ticket.service.http.parameter.EntityParameter;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import com.drama.train.ticket.service.http.parameter.HttpPostParameter;
import com.drama.train.ticket.service.http.parameter.IHttpParameter;
import com.drama.train.ticket.service.http.request.HttpPostRequest;
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
public class Http12306CheckOrderAction extends AbstractHttpAction {
    public final static String NAME_PARAM_INITDC = "initDc";
    public final static String NAME_PARAM_PASSENGER = "passenger";
    private final BuyTicketConfig config;

    public Http12306CheckOrderAction(Http12306Config http12306Config, BuyTicketConfig config) {
        super(http12306Config);
        this.config = config;
    }

    /**
     *
     * @param httpParameter 12个参数
     * cancel_flag: 2
     * bed_level_order_num: 000000000000000000000000000000
     * passengerTicketStr: O,0,1,陈锐均,1,4451***********650,18664844539,N,216c20718a316cc5de0ed7027a18b9151425eab660a56e8024888b4773a7f3ab8cb5c6a6e3ccb6de39b8ce423fe63948
     * oldPassengerStr: 陈锐均,1,4451***********650,1_
     * tour_flag: dc
     * randCode:
     * whatsSelect: 1
     * sessionId:
     * sig:
     * scene: nc_login
     * _json_att:
     * REPEAT_SUBMIT_TOKEN: 02b237b203fc9eee7109d2166d7db74e
     * @return
     */
    @Override
    public URI prepareUrl(HttpParameter httpParameter) {
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(http12306Config.getScheme())
                    .setHost(http12306Config.getHost())
                    .setPath(http12306Config.getCheckOrderUrl());
                return uriBuilder.build();
        } catch (URISyntaxException e) {
            log.error("无法获取url："+e.getMessage(), e);
        }
        return null;
    }

    public static CheckOrderEntity getCheckOrderEntity(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CheckOrderEntity entity = null;
        entity = mapper.readValue(json, CheckOrderEntity.class);
        return entity;
    }

    public static String getPassengerTicketStr(PassengerEntity passengerEntity, String seatType){
        String seat = HttpService.getSeatTypeCode(seatType);
        return seat +
                ",0,1," +    //1是车票类型,表示成人票
                passengerEntity.getPassenger_name() + "," +
                "1," +   //1是指二代身份证
                passengerEntity.getPassenger_id_no() + "," +
                passengerEntity.getMobile_no() + ",N," +
                passengerEntity.getAllEncStr();
    }

    public static String getOldPassengerStr(PassengerEntity passengerEntity){
        return passengerEntity.getPassenger_name() + "," +
                "1," +   //1是指二代身份证
                passengerEntity.getPassenger_id_no() + ",1_";
    }

    @Override
    public IHttpRequest prepareHttpRequest() {
        return new HttpPostRequest();
    }

    @Override
    public IHttpHeader prepareHttpHeader() {
        return new OrderHttpHeader(http12306Config);
    }

    @Override
    public IHttpParameter prepareHttpParameter() {
        return new HttpPostParameter();
    }

    @Override
    public HttpResult action(HttpClientContext context, CookieStore cookieStore, ActionParameter actionParameter) {
        if(actionParameter==null){
            log.info("没有传递参数给检查订单请求");
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递参数给检查订单请求");
        }
        PassengerEntity passengerEntity = actionParameter.getValue(NAME_PARAM_PASSENGER);
        InitDcEntity initDcEntity = actionParameter.getValue(NAME_PARAM_INITDC);
        if(passengerEntity==null){
            log.info("没有传递乘客信息给检查订单请求");
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递乘客信息给检查订单请求");
        }
        if(initDcEntity==null){
            log.info("没有传递单程票信息给检查订单请求");
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("没有传递单程票信息给检查订单请求");
        }

        HttpParameter httpParameter = HttpParameter.of()
                .addParameter("cancel_flag","2")
                .addParameter("bed_level_order_num","000000000000000000000000000000")
                .addParameter("passengerTicketStr",Http12306CheckOrderAction.getPassengerTicketStr(passengerEntity, config.getSeatType()))
                .addParameter("oldPassengerStr",Http12306CheckOrderAction.getOldPassengerStr(passengerEntity))
                .addParameter("tour_flag",config.getTourFlag())
                .addParameter("randCode","")
                .addParameter("whatsSelect","1")
                .addParameter("sessionId","")
                .addParameter("sig","")
                .addParameter("scene","nc_login")
                .addParameter("_json_att","")
                .addParameter("REPEAT_SUBMIT_TOKEN",initDcEntity.getGlobalRepeatSubmitToken());
        HttpResult httpResult = doRequest(context, cookieStore, httpParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.HTTP_OK){
            log.info("无法检查订单");
            return httpResult;
        }

        try{
            CheckOrderEntity checkOrderEntity = getCheckOrderEntity(httpResult.getResponseText());
            if(!checkOrderEntity.isStatus()){
                log.info("预订订单提交失败，"+httpResult.getResponseText());
                httpResult.setResultResponseError("预订订单提交失败");
                return httpResult;
            }else if(!checkOrderEntity.getData().isSubmitStatus() || !checkOrderEntity.getData().getChoose_Seats().contains(HttpService.getSeatTypeCode(config.getSeatType()))){
                String msg =config.getSeatType()+"没票";
                log.info(msg+","+httpResult.getResponseText());
                httpResult.setResultInterrupt(msg);
                return httpResult;
            }else
                httpResult.setResultResponseOK(checkOrderEntity);
//            else if(!checkOrderEntity.getData().getCanChooseSeats().equals("Y")){
//                log.info("不可选座票");
//                return null;
//            }
            return httpResult;
        } catch (JsonProcessingException e) {
            log.error(httpResult.getResponseText());
            log.error(e.getMessage(), e);
            httpResult.setResultSendFail("无法解析检查订单信息:"+e.getMessage());
            return httpResult;
        }
    }
}
