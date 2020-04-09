package com.drama.train.ticket.service;

import org.apache.http.client.protocol.HttpClientContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HttpContextCacheService {
    Map<String, HttpClientContext> map = new ConcurrentHashMap<>();

    public void putContext(String uuid, HttpClientContext context){
        map.put(uuid, context);
    }

    public HttpClientContext getContext(String uuid){
        return map.get(uuid);
    }
}
