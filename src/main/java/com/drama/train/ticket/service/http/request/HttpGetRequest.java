package com.drama.train.ticket.service.http.request;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import java.net.URI;

public class HttpGetRequest implements IHttpRequest {
    @Override
    public HttpRequestBase getHttpRequest(URI url) {
        return new HttpGet(url);
    }
}
