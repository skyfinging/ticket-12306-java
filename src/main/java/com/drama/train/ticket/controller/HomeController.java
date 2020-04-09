package com.drama.train.ticket.controller;

import com.drama.train.ticket.config.BuyTicketConfig;
import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.AuthUamtkEntity;
import com.drama.train.ticket.entity.ConfEntity;
import com.drama.train.ticket.service.HttpService;
import com.drama.train.ticket.service.MailServiceImpl;
import com.drama.train.ticket.service.StationNameService;
import com.drama.train.ticket.service.http.*;
import com.drama.train.ticket.task.BuyTicketTask;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@Controller
public class HomeController {

    @Value("${buy.ticket.auto}")
    private boolean isAutoBuyTicket;

    @Autowired
    Http12306Config http12306Config;
    @Autowired
    HttpService httpService;
    @Autowired
    BuyTicketConfig buyTicketConfig;
    @Autowired
    ThreadPoolTaskExecutor executor;
    @Autowired
    MailServiceImpl mailService;
    @Autowired
    StationNameService stationNameService;

    @RequestMapping("/index")
    @ResponseBody
    public String index(HttpServletRequest request){
        Http12306CheckQrAction checkQrAction = new Http12306CheckQrAction(http12306Config, httpService);
        Http12306ConfAction confAction = new Http12306ConfAction(http12306Config);
        Http12306AuthUamtkAction uamtkAction = new Http12306AuthUamtkAction(http12306Config, httpService);
        Http12306ClientAction clientAction = new Http12306ClientAction(http12306Config);

        String uuid = HttpService.getUUid(request);
        if(uuid==null){
            return "请扫描登陆";
        }
        HttpClientContext context = httpService.getContext(uuid);
        CookieStore cookieStore = httpService.getCookieStore(uuid);

        ActionParameter actionParameter = ActionParameter.of().addParameter(Http12306CheckQrAction.NAME_PARAM_UUID, uuid);
        HttpResult httpResult = checkQrAction.action(context, cookieStore, actionParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.RESPONSE_OK){
            return httpResult.getInfo();
        }

        httpResult = uamtkAction.action(context, cookieStore, null);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.RESPONSE_OK){
            return httpResult.getInfo();
        }

        AuthUamtkEntity authUamtkEntity = (AuthUamtkEntity) httpResult.getEntity();
        actionParameter = ActionParameter.of().addParameter(Http12306ClientAction.NAME_PARAM_TK, authUamtkEntity.getNewapptk());
        httpResult = clientAction.action(context, cookieStore, actionParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.RESPONSE_OK){
            return httpResult.getInfo();
        }

        httpResult = confAction.action(context, cookieStore, ActionParameter.of());
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.RESPONSE_OK){
            return httpResult.getInfo();
        }
        ConfEntity confEntity = (ConfEntity) httpResult.getEntity();
        log.info(confEntity.getData().getName()+"登陆成功");

        BuyTicketTask buyTicketTask = new BuyTicketTask(http12306Config, buyTicketConfig, httpService, mailService, stationNameService, uuid);
        buyTicketTask.setBuyTimeBegin(http12306Config.getBuyTimeBegin());
        buyTicketTask.setBuyTimeEnd(http12306Config.getBuyTimeEnd());
        if(isAutoBuyTicket) {
            log.info("启动定时购票流程");
            executor.execute(buyTicketTask);
        }
        return confEntity.getData().getName()+"登陆成功";
    }
}
