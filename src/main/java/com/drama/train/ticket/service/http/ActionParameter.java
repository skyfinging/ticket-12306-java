package com.drama.train.ticket.service.http;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Http请求的参数，保存键值对，当发起请求的时候，把键值对设置到Http请求中
 */
@Getter
public class ActionParameter {
    Map<String, Object> params = new HashMap<>();

    public static ActionParameter of(){
        return new ActionParameter();
    }

    public ActionParameter addParameter(String name, Object value){
        params.put(name, value);
        return this;
    }

    public <T> T getValue(String name){
        return (T) params.get(name);
    }
}
