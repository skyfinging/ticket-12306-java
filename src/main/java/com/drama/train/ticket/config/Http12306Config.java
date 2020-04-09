package com.drama.train.ticket.config;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * 12306网站爬虫配置，定义各请求的URL
 */
@Configuration
@Log4j2
@Getter
public class Http12306Config {

    @Value("${http.scheme}")
    private String scheme;

    @Value("${host}")
    private String host;

    @Value("${12306.appid}")
    private String appid;

    @Value("${12306.conf.url}")
    private String confUrl;

    @Value("${12306.uamtk.url}")
    private String uamtkUrl;

    @Value("${12306.login.banner.url}")
    private String bannerUrl;

    @Value("${create.qr.url}")
    private String createQrUrl;

    @Value("${check.qr.url}")
    private String checkqrUrl;

    @Value("${auth.uamtk.url}")
    private String authUamtkUrl;

    @Value("${uamuthclient.url}")
    private String uamuthClientUrl;

    @Value("${12306.login.referer.url}")
    private String loginReferer;

    @Value("${query.url}")
    private String queryUrl;

    @Value("${12306.ticket.referer.url}")
    private String ticketReferer;

    @Value("${check.user.url}")
    private String checkUserUrl;

    @Value("${submit.order.url}")
    private String submitOrderUrl;

    @Value("${initDc.url}")
    private String initDcUrl;

    @Value("${dynamicJs.url}")
    private String dynamicJsUrl;

    @Value("${passenger.dto.url}")
    private String passengerDtoUrl;

    @Value("${check.order.url}")
    private String checkOrderUrl;

    @Value("${12306.order.referer.url}")
    private String orderRefererUrl;

    @Value("${queue.count.url}")
    private String queueCountUrl;

    @Value("${confirm.url}")
    private String confirmUrl;

    @Value("${keep.alive.url}")
    private String keyAliveUrl;

    @Value("${buy.time.begin}")
    String buyTimeBegin;
    @Value("${buy.time.end}")
    String buyTimeEnd;

    @Value("${cookie.device.code}")
    String cookieDeviceCode;

    public String getOrigin(){
        return scheme+"://"+host;
    }

    public String getLoginReferer(){
        return scheme+"://"+host+loginReferer;
    }

    public String getTicketReferer(){
        return scheme+"://"+host+ticketReferer;
    }

    public String getOrderReferer(){
        return scheme+"://"+host+orderRefererUrl;
    }
}
