package com.drama.train.ticket.service.http.parameter;

import lombok.Getter;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HttpParameter {
    List<NameValuePair> list = new ArrayList<>();

    public static HttpParameter of(){
        return new HttpParameter();
    }

    public HttpParameter addParameter(String name, String value){
        list.add(new BasicNameValuePair(name, value));
        return this;
    }

    public URIBuilder setParameterToURL(URIBuilder urlBuilder){
        list.forEach(n->urlBuilder.setParameter(n.getName(),n.getValue()));
        return urlBuilder;
    }
}
