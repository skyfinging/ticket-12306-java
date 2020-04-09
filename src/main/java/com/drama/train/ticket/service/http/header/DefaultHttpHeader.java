package com.drama.train.ticket.service.http.header;

import com.drama.train.ticket.config.Http12306Config;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class DefaultHttpHeader implements IHttpHeader {

    private final Http12306Config http12306Config;

    public DefaultHttpHeader(Http12306Config http12306Config) {
        this.http12306Config = http12306Config;
    }

    @Override
    public List<NameValuePair> getHeader() {
        List<NameValuePair> list = createDefaultGetHeader(http12306Config.getOrigin());
        list.add(new BasicNameValuePair("Referer",http12306Config.getLoginReferer()));
        return list;
    }

    static List<NameValuePair> createDefaultGetHeader(String origin){
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("Content-type", "application/json;charset=utf-8"));
        list.add(new BasicNameValuePair("Connection", "keep-alive"));
        list.add(new BasicNameValuePair("Origin",origin));
        return list;
    }

    static List<NameValuePair> createDefaultPostHeader(String origin){
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("Content-type", "application/x-www-form-urlencoded; charset=utf-8"));
        list.add(new BasicNameValuePair("Connection", "keep-alive"));
        list.add(new BasicNameValuePair("Origin",origin));
        return list;
    }
}
