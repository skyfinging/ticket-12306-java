package com.drama.train.ticket.service.http.request;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import java.net.URI;

public class HttpPostRequest implements IHttpRequest {
    @Override
    public HttpRequestBase getHttpRequest(URI url) {
        return new HttpPost(url);
    }
}
