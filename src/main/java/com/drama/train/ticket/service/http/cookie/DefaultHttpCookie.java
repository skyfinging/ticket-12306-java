package com.drama.train.ticket.service.http.cookie;

import org.apache.http.client.CookieStore;

public class DefaultHttpCookie implements IHttpCookie {
    @Override
    public CookieStore createCookieStore(CookieStore cookieStore) {
        return cookieStore;
    }
}
