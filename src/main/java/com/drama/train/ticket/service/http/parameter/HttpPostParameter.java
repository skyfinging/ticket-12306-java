package com.drama.train.ticket.service.http.parameter;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;

public class HttpPostParameter implements IHttpParameter {
    @Override
    public HttpEntity getParameter(HttpParameter httpParameter) {
        if(httpParameter ==null)
            return null;
        UrlEncodedFormEntity refe = new UrlEncodedFormEntity(httpParameter.getList(), Consts.UTF_8);
        return refe;
    }
}
