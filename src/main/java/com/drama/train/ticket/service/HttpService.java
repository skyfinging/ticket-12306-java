package com.drama.train.ticket.service;

import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.CheckQREntity;
import com.drama.train.ticket.entity.QRCodeEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
public class HttpService {

    @Autowired
    Http12306Config http12306Config;

    Map<String, HttpClientContext> contextMap = new ConcurrentHashMap<>();
    Map<String, CookieStore> cookieMap = new ConcurrentHashMap<>();

    public Cookie createCookie(Cookie cookie){
        BasicClientCookie newCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
        newCookie.setVersion(0);
        newCookie.setPath("/");
        newCookie.setAttribute("path","/");
        newCookie.setDomain(http12306Config.getHost());
        return newCookie;
    }

    public String getAppId(){
        return http12306Config.getAppid();
    }

    public static String getUUid(HttpServletRequest request){
        HttpSession session = request.getSession();
        if(session!=null){
            return (String) session.getAttribute("UUID");
        }
        return null;
    }

    public static void setUUid(HttpServletRequest request, String uuid){
        HttpSession session = request.getSession();
        if(session!=null){
            session.setAttribute("UUID", uuid);
        }
    }

    public void putContext(String uuid, HttpClientContext context){
        contextMap.put(uuid, context);
    }

    public HttpClientContext getContext(String uuid){
        if(uuid==null)
            return null;
        return contextMap.get(uuid);
    }

    public void putCookieStore(String uuid, CookieStore cookieStore){
        cookieMap.put(uuid, cookieStore);
    }

    public CookieStore getCookieStore(String uuid){
        if(uuid==null)
            return null;
        return cookieMap.get(uuid);
    }

    public static String getSeatTypeCode(String seatName){
        if(!"二等座".equals(seatName)){
            throw new IllegalArgumentException("不支持其他席位");
        }
        return "O";
    }

    public String getCookieDeviceCode(){
        return http12306Config.getCookieDeviceCode();
    }
}
