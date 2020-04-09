package com.drama.train.ticket.service.http.context;

import org.apache.http.client.protocol.HttpClientContext;

public class CreateHttpContext implements IHttpContext {
    @Override
    public HttpClientContext creatHttpContext(HttpClientContext context) {
        return HttpClientContext.create();
    }
}
