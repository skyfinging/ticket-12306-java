package com.drama.train.ticket.service.http.cookie;

import com.drama.train.ticket.config.BuyTicketConfig;
import com.drama.train.ticket.service.HttpService;
import com.drama.train.ticket.util.EscapeUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

/**
 * _jc_save_toStation=%u6F6E%u6C55%2CCBQ;
 * _jc_save_wfdc_flag=dc;
 * _jc_save_fromDate=2020-02-04;
 * _jc_save_fromStation=%u5E7F%u5DDE%u4E1C%2CGGQ;
 * _jc_save_toDate=2020-01-06
 */
@Log4j2
public class TicketHttpCookie implements IHttpCookie {

    final HttpService httpService;
    final BuyTicketConfig buyTicketConfig;

    public TicketHttpCookie(HttpService httpService, BuyTicketConfig buyTicketConfig) {
        this.httpService = httpService;
        this.buyTicketConfig = buyTicketConfig;
    }

    @Override
    public CookieStore createCookieStore(CookieStore cookieStore) {
        if(cookieStore==null)
            cookieStore = new BasicCookieStore();
        Cookie cookie1 = new BasicClientCookie("_jc_save_toStation", EscapeUtils.escape(buyTicketConfig.getToStation() + "," + buyTicketConfig.getToStationCode()));
        cookieStore.addCookie(httpService.createCookie(cookie1));
        Cookie cookie2 = new BasicClientCookie("_jc_save_fromStation",  EscapeUtils.escape(buyTicketConfig.getFromStation()+","+buyTicketConfig.getFromStationCode()));
        cookieStore.addCookie(httpService.createCookie(cookie2));
        Cookie cookie3 = new BasicClientCookie("_jc_save_wfdc_flag",buyTicketConfig.getTourFlag());
        cookieStore.addCookie(httpService.createCookie(cookie3));
        Cookie cookie4 = new BasicClientCookie("_jc_save_fromDate",buyTicketConfig.getTrainDate());
        cookieStore.addCookie(httpService.createCookie(cookie4));
        Cookie cookie5 = new BasicClientCookie("_jc_save_toDate",buyTicketConfig.getTrainDate());
        cookieStore.addCookie(httpService.createCookie(cookie5));
        return cookieStore;
    }
}
