package com.drama.train.ticket.service.http.cookie;

import com.drama.train.ticket.service.HttpService;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

public class DeviceHttpCookie implements IHttpCookie {

    final HttpService httpService;

    public DeviceHttpCookie(HttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    public CookieStore createCookieStore(CookieStore cookieStore) {
        if(cookieStore==null)
            cookieStore = new BasicCookieStore();
        Cookie cookie = new BasicClientCookie("RAIL_DEVICEID",httpService.getCookieDeviceCode());
        cookieStore.addCookie(httpService.createCookie(cookie));
        cookieStore.addCookie(new BasicClientCookie("RAIL_EXPIRATION",""+(System.currentTimeMillis()-1000)));
        return cookieStore;
    }
}
