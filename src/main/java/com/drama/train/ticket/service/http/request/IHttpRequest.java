package com.drama.train.ticket.service.http.request;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;

import java.net.CookieStore;
import java.net.URI;

public interface IHttpRequest {

    HttpRequestBase getHttpRequest(URI url);
}
