package com.drama.train.ticket.service.http.context;

import org.apache.http.client.protocol.HttpClientContext;

public class DefaultHttpContext implements IHttpContext {
    @Override
    public HttpClientContext creatHttpContext(HttpClientContext context) {
        return context;
    }
}
