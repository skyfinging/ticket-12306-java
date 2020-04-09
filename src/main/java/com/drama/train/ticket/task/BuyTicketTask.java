package com.drama.train.ticket.task;

import com.drama.train.ticket.bean.TrainInfo;
import com.drama.train.ticket.config.BuyTicketConfig;
import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.ConfEntity;
import com.drama.train.ticket.entity.InitDcEntity;
import com.drama.train.ticket.entity.PassengerEntity;
import com.drama.train.ticket.entity.QueryZEntity;
import com.drama.train.ticket.service.HttpService;
import com.drama.train.ticket.service.MailServiceImpl;
import com.drama.train.ticket.service.StationNameService;
import com.drama.train.ticket.service.http.*;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

@Log4j2
public class BuyTicketTask implements Runnable {
    final Http12306Config http12306Config;
    final BuyTicketConfig config;
    final HttpService httpService;
    final MailServiceImpl mailService;
    final StationNameService stationNameService;
    private LocalTime buyTimeBegin;
    private LocalTime buyTimeEnd;

    private final String uuid;
    private HttpClientContext context;
    private CookieStore cookieStore;

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private Http12306QueryAction queryAction;
    private Http12306CheckUserAction checkUserAction;
    private Http12306SubmitOrderAction submitOrderAction;
    private Http12306InitDcAction initDcAction;
    private Http12306PassengerDtoAction passengerDtoAction;
    private Http12306CheckOrderAction checkOrderAction;
    private Http12306GetQueueCountAction getQueueCountAction;
    private Http12306ConfirmAction confirmAction;
    private Http12306KeepAliveAction keepAliveAction;
    private Http12306ConfAction confAction;

    public BuyTicketTask(Http12306Config http12306Config, BuyTicketConfig config, HttpService httpService,
                         MailServiceImpl mailService, StationNameService stationNameService, String uuid) {
        this.http12306Config = http12306Config;
        this.config = config;
        this.httpService = httpService;
        this.mailService = mailService;
        this.stationNameService = stationNameService;
        this.uuid = uuid;

        context = httpService.getContext(uuid);
        cookieStore = httpService.getCookieStore(uuid);

        queryAction = new Http12306QueryAction(http12306Config,httpService,config);
        checkUserAction = new Http12306CheckUserAction(http12306Config);
        submitOrderAction = new Http12306SubmitOrderAction(http12306Config, config);
        initDcAction = new Http12306InitDcAction(http12306Config);
        passengerDtoAction = new Http12306PassengerDtoAction(http12306Config, config);
        checkOrderAction = new Http12306CheckOrderAction(http12306Config, config);
        getQueueCountAction = new Http12306GetQueueCountAction(http12306Config, config);
        confirmAction = new Http12306ConfirmAction(http12306Config, config);

        keepAliveAction = new Http12306KeepAliveAction(http12306Config, httpService);
        confAction = new Http12306ConfAction(http12306Config);
    }

    public void setBuyTimeBegin(String time){
        buyTimeBegin = LocalTime.parse(time, dateTimeFormatter);
    }

    public void setBuyTimeEnd(String time){
        buyTimeEnd = LocalTime.parse(time, dateTimeFormatter);
    }

    /**
     * 发送心跳包，保持登陆状态
     * @return
     */
    private boolean keepAlive(){
        keepAliveAction.action(context, cookieStore, null);
        HttpResult httpResult = confAction.action(context, cookieStore, ActionParameter.of());
        if(httpResult.getResultStatus()!= HttpResultStatusEnum.RESPONSE_OK){
            log.info("账号已退出登录");
            log.info(httpResult.getResponseText());
            return false;
        }
        ConfEntity confEntity = (ConfEntity) httpResult.getEntity();
        return confEntity.getData().getIs_login().equals("Y");
    }

    /**
     * 买票操作，如果买票失败，可能是程序出错，那么直接退出；也可能是余票不够，需要重试
     * 正常情况下，刷余票信息，有余票就买票
     * 当时间快到放票时间，则不进入定时，这时候设定睡眠时间，去抢票
     * @param needDiGui
     * @return
     */
    private boolean buyTicket(boolean needDiGui){
        HttpResult httpResult = sendRequestToBuyTicket();
        switch (httpResult.getResultStatus()){
            case RESPONSE_OK:
            case HTTP_SEND_FAIL:
                return false;
            case INTERRUPT:
                if(needDiGui) {
                    //没有车票的情况下，需要等60秒之后再重试
                    LocalTime now = LocalTime.now();
                    if (now.getMinute() == 59 || now.getMinute() == 29) {
                        try {
                            Thread.sleep((60-now.getSecond())*1000-now.get(ChronoField.MILLI_OF_SECOND));
                        } catch (InterruptedException ignored) {
                        }
                        //在整点或半点的时候，可能是放票的时间点，需要整点抢票试试
                        boolean needRetry = buyTicket(false);   //第一次抢票可能刚好是整点，票还没放出来，但是下一刻票就放出来了，需要再抢一次
                        if(needRetry)
                            return buyTicket(false);
                        return false;
                    } else {
                        //普通时候，每隔1分钟刷一下票
                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException ignored) {
                        }
                        return keepAlive();
                    }
                }else
                    return true;
            default:
                return true;
        }
    }

    /**
     * 抢一次票，返回抢票结果
     * @return
     */
    private HttpResult sendRequestToBuyTicket(){
        //查余票信息
        HttpResult httpResult = queryAction.action(context, cookieStore, null);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.RESPONSE_OK)
            return httpResult;
        QueryZEntity queryZEntity = (QueryZEntity) httpResult.getEntity();

        int skipTrain = 0;
        TrainInfo lastTrainInfo = null;
        HttpResult lastHttpResult = null;
        String[] trainCodes = config.getTrainCode().split(",");
        //可能有多个车次可购票，都尝试一下
        while(true) {
            //从余票信息中筛选出一趟可以购买的车次
            TrainInfo trainInfo = selectTrain(queryZEntity, trainCodes, config.getSeatType(), skipTrain);
            skipTrain++;
            if (trainInfo == null) {
                if(lastTrainInfo==null) {
                    log.info(queryZEntity);
                    log.info("找不到可以购买的车次信息：" + config.getTrainCode());
                    return new HttpResult(null, context, cookieStore, null, "")
                            .setResultInterrupt("找不到可以购买的车次信息：" + config.getTrainCode());
                }else{
                    return lastHttpResult;
                }
            }
            lastTrainInfo = trainInfo;
            log.info("尝试购买："+trainInfo);

            //提交预订订单
            ActionParameter actionParameter = ActionParameter.of().addParameter(Http12306SubmitOrderAction.NAME_PARAM_TRAININFO, trainInfo);
            lastHttpResult = submitOrderAction.action(context, cookieStore, actionParameter);
            if (lastHttpResult.getResultStatus() != HttpResultStatusEnum.RESPONSE_OK)
                continue;

            //模仿浏览器发送initDc请求
            lastHttpResult = initDcAction.action(context, cookieStore, null);
            if (lastHttpResult.getResultStatus() != HttpResultStatusEnum.RESPONSE_OK)
                continue;

            //获取乘客信息
            InitDcEntity initDcEntity = (InitDcEntity) lastHttpResult.getEntity();
            actionParameter = ActionParameter.of().addParameter(Http12306PassengerDtoAction.NAME_PARAM_INITDC, initDcEntity);
            lastHttpResult = passengerDtoAction.action(context, cookieStore, actionParameter);
            if (lastHttpResult.getResultStatus() != HttpResultStatusEnum.RESPONSE_OK)
                continue;

            //确认乘客订单
            PassengerEntity passenger = (PassengerEntity) lastHttpResult.getEntity();
            actionParameter = ActionParameter.of().addParameter(Http12306CheckOrderAction.NAME_PARAM_INITDC, initDcEntity)
                    .addParameter(Http12306CheckOrderAction.NAME_PARAM_PASSENGER, passenger);
            lastHttpResult = checkOrderAction.action(context, cookieStore, actionParameter);
            if (lastHttpResult.getResultStatus() != HttpResultStatusEnum.RESPONSE_OK)
                continue;

            //查询订单排队状态
            actionParameter = ActionParameter.of().addParameter(Http12306GetQueueCountAction.NAME_PARAM_INITDC, initDcEntity)
                    .addParameter(Http12306GetQueueCountAction.NAME_PARAM_TRAININFO, trainInfo);
            lastHttpResult = getQueueCountAction.action(context, cookieStore, actionParameter);
            if (lastHttpResult.getResultStatus() != HttpResultStatusEnum.RESPONSE_OK)
                continue;

            //提交订单，购票成功
            actionParameter = ActionParameter.of().addParameter(Http12306ConfirmAction.NAME_PARAM_INITDC, initDcEntity)
                    .addParameter(Http12306ConfirmAction.NAME_PARAM_PASSENGER, passenger);
            lastHttpResult = confirmAction.action(context, cookieStore, actionParameter);
            //如果购票成功，则退出，否则继续购买下一趟车次
            if (lastHttpResult.getResultStatus() == HttpResultStatusEnum.RESPONSE_OK) {
                log.info("出票成功,车次：" + trainInfo.getTrainCode());
                mailService.sendSimpleMail(
                        config.getNoticeMail(),
                        "出票成功",
                        "下单成功，车次"+trainInfo.getTrainCode()
                                +"，出发时间："+config.getTrainDate()+" "+trainInfo.getBeginTime()
                                +"，出发站:"+stationNameService.getStationName(trainInfo.getBoardStation())
                                +"，请及时在半小时内支付");
                return new HttpResult(null, context, cookieStore, null, "")
                        .setResultResponseOK("出票成功，车次：" + trainInfo.getTrainCode());
            }
        }
    }

    private TrainInfo selectTrain(QueryZEntity queryZEntity, String[] trainCodes, String seatName, final int skipTrain){
        if(trainCodes==null || trainCodes.length==0)
            return null;
        int skipTrain1 = skipTrain;
        List<String> codes = Arrays.asList(trainCodes);
        TrainInfo tmp = null;
        for(String trainCode : trainCodes) {
            TrainInfo trainInfo = queryZEntity.getTrainInfo(trainCode);
            if(trainInfo==null)
                continue;
            tmp = trainInfo;
            String seatNum = trainInfo.getSeatNum(seatName);
            if(!seatNum.isEmpty() && !seatNum.equals("无") && !seatNum.equals("*")){
                if(skipTrain1>0){
                    skipTrain1--;
                    continue;
                }
                return trainInfo;
            }
        }
        if(tmp==null){
            log.error("无法匹配到车次信息，请检查车次设置："+codes.stream().collect(Collectors.joining(",")));
        }
        return null;
    }

    @Override
    public void run() {
        while(true) {
            LocalTime current = LocalTime.now();
            //当前时间在有效时间内才能购买
            if (current.isAfter(buyTimeBegin) && current.isBefore(buyTimeEnd)) {
                boolean needRetry = buyTicket(true);
                if(!needRetry)
                    break;
            } else {
                if(!keepAlive())
                    break;
                if(current.isAfter(buyTimeEnd)){
                    try {
                        sleep(60000);
                    } catch (InterruptedException ignored) {
                    }
                }else if(isBeforeAndSleep(current, buyTimeBegin, 60000)
                    && isBeforeAndSleep(current, buyTimeBegin, 5000)
                    && isBeforeAndSleep(current, buyTimeBegin, 1000)){
                    try {
                        sleep(10);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
    }

    /**
     * 判断当前时间是否比预定时间早time毫秒
     * 如果当前时间比开始时间早，就进行等待，等待时间也是time毫秒，避免错过抢票时间
     * @param localTime1
     * @param localTime2
     * @param time
     * @return
     */
    private boolean isBeforeAndSleep(LocalTime localTime1, LocalTime localTime2, long time){
        LocalTime localTime = localTime2.minus(time, ChronoUnit.MILLIS);
        if(localTime1.isBefore(localTime)){
            try {
                sleep(time);
            } catch (InterruptedException ignore) {
            }
            return false;
        }
        return true;
    }
}
