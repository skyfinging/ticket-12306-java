package com.drama.train.ticket.service.http.header;

import com.drama.train.ticket.config.Http12306Config;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;

public class TicketHttpHeader implements IHttpHeader {

    final Http12306Config http12306Config;

    public TicketHttpHeader(Http12306Config http12306Config) {
        this.http12306Config = http12306Config;
    }

    @Override
    public List<NameValuePair> getHeader() {
        List<NameValuePair> list = DefaultHttpHeader.createDefaultGetHeader(http12306Config.getOrigin());
        list.add(new BasicNameValuePair("Referer",http12306Config.getTicketReferer()));
        return list;
    }
}
