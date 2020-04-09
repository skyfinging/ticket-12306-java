package com.drama.train.ticket.service.http;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;

import java.net.URI;

/**
 * Http请求同一返回结果，以方便后续处理
 */
@Getter
@Accessors(chain = true)
public class HttpResult {
    final private URI url;                              //请求的URL
    final private HttpClientContext httpContext;        //Http请求关联的上下文信息
    final private CookieStore cookieStore;              //cookie信息
    final private StatusLine responseStatusLine;        //Http返回的状态
    final private String responseText;                  //请求返回的原始内容

    private String info;                                //提示信息
    private HttpResultStatusEnum resultStatus;          //请求返回对应的业务状态，根据业务状态判断流程中断还是继续处理
    private Object entity;                              //返回值对应的业务Entity对象

    public HttpResult(URI url, HttpClientContext httpContext, CookieStore cookieStore, StatusLine responseStatusLine, String responseText) {
        this.url = url;
        this.httpContext = httpContext;
        this.cookieStore = cookieStore;
        this.responseStatusLine = responseStatusLine;
        this.responseText = responseText;
    }

    public HttpResult setResultSendFail(String info){
        this.info = info;
        this.resultStatus = HttpResultStatusEnum.HTTP_SEND_FAIL;
        return this;
    }

    public HttpResult setResultFail(String info){
        this.info = info;
        this.resultStatus = HttpResultStatusEnum.HTTP_FAIL;
        return this;
    }

    public HttpResult setResultOk(String info){
        this.info = info;
        this.resultStatus = HttpResultStatusEnum.HTTP_OK;
        return this;
    }

    public HttpResult setResultResponseError(String info){
        this.info = info;
        this.resultStatus = HttpResultStatusEnum.RESPONSE_ERROR;
        return this;
    }

    public HttpResult setResultResponseOK(Object entity){
        this.resultStatus = HttpResultStatusEnum.RESPONSE_OK;
        this.entity = entity;
        return this;
    }

    public HttpResult setResultInterrupt(String info){
        this.info = info;
        this.resultStatus = HttpResultStatusEnum.INTERRUPT;
        return this;
    }

    @Override
    public String toString(){
        return "[url]"+url
                +",[status]" +responseStatusLine
                +",[response]"+responseText
                +",[info]"+info;
    }
}
