package com.drama.train.ticket.controller;

import com.drama.train.ticket.bean.TrainInfo;
import com.drama.train.ticket.config.BuyTicketConfig;
import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.*;
import com.drama.train.ticket.service.HttpService;
import com.drama.train.ticket.service.MailServiceImpl;
import com.drama.train.ticket.service.http.*;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@Controller
public class BuyTicketController {

    @Autowired
    Http12306Config http12306Config;
    @Autowired
    BuyTicketConfig config;
    @Autowired
    HttpService httpService;

    @RequestMapping("/buy")
    @ResponseBody
    public String buyTicket(HttpServletRequest request){
        String uuid = HttpService.getUUid(request);
        HttpClientContext context = httpService.getContext(uuid);
        CookieStore cookieStore = httpService.getCookieStore(uuid);

        HttpResult httpResult = doBuyTicket(uuid, context, cookieStore);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.RESPONSE_OK){
            return httpResult.getInfo();
        }

        return "出票成功";
    }

    private HttpResult doBuyTicket(String uuid, HttpClientContext context, CookieStore cookieStore){
        Http12306QueryAction queryAction = new Http12306QueryAction(http12306Config,httpService,config);
        Http12306CheckUserAction checkUserAction = new Http12306CheckUserAction(http12306Config);
        Http12306SubmitOrderAction submitOrderAction = new Http12306SubmitOrderAction(http12306Config, config);
        Http12306InitDcAction initDcAction = new Http12306InitDcAction(http12306Config);
        Http12306DynamicjsAction dynamicjsAction = new Http12306DynamicjsAction(http12306Config);
        Http12306PassengerDtoAction passengerDtoAction = new Http12306PassengerDtoAction(http12306Config, config);
        Http12306CheckOrderAction checkOrderAction = new Http12306CheckOrderAction(http12306Config, config);
        Http12306GetQueueCountAction getQueueCountAction = new Http12306GetQueueCountAction(http12306Config, config);
        Http12306ConfirmAction confirmAction = new Http12306ConfirmAction(http12306Config, config);


        HttpResult httpResult = queryAction.action(context, cookieStore, null);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.RESPONSE_OK)
            return httpResult;
        QueryZEntity queryZEntity = (QueryZEntity) httpResult.getEntity();
        TrainInfo trainInfo = queryZEntity.getTrainInfo(config.getTrainCode());
        if(trainInfo==null){
            log.info(queryZEntity);
            log.info("找不到对应车次信息"+config.getTrainCode());
            return new HttpResult(null, context, cookieStore, null, "")
                    .setResultSendFail("找不到对应车次信息"+config.getTrainCode());
        }
        log.info(trainInfo);

        ActionParameter actionParameter = ActionParameter.of().addParameter(Http12306SubmitOrderAction.NAME_PARAM_TRAININFO, trainInfo);
        httpResult = submitOrderAction.action(context, cookieStore, actionParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.RESPONSE_OK)
            return httpResult;

        httpResult = initDcAction.action(context, cookieStore, null);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.RESPONSE_OK)
            return httpResult;

        InitDcEntity initDcEntity = (InitDcEntity) httpResult.getEntity();
        actionParameter = ActionParameter.of().addParameter(Http12306PassengerDtoAction.NAME_PARAM_INITDC, initDcEntity);
        httpResult = passengerDtoAction.action(context, cookieStore, actionParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.RESPONSE_OK)
            return httpResult;

        PassengerEntity passenger = (PassengerEntity) httpResult.getEntity();
        actionParameter =  ActionParameter.of().addParameter(Http12306CheckOrderAction.NAME_PARAM_INITDC, initDcEntity)
                .addParameter(Http12306CheckOrderAction.NAME_PARAM_PASSENGER, passenger);
        httpResult = checkOrderAction.action(context, cookieStore, actionParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.RESPONSE_OK)
            return httpResult;

        actionParameter =  ActionParameter.of().addParameter(Http12306GetQueueCountAction.NAME_PARAM_INITDC, initDcEntity)
                .addParameter(Http12306GetQueueCountAction.NAME_PARAM_TRAININFO, trainInfo);
        httpResult = getQueueCountAction.action(context, cookieStore, actionParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.RESPONSE_OK)
            return httpResult;

        actionParameter =  ActionParameter.of().addParameter(Http12306ConfirmAction.NAME_PARAM_INITDC, initDcEntity)
                .addParameter(Http12306ConfirmAction.NAME_PARAM_PASSENGER, passenger);
        httpResult = confirmAction.action(context, cookieStore, actionParameter);
        return httpResult;
    }

}
