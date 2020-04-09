package com.drama.train.ticket.service.http;

import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.service.http.context.DefaultHttpContext;
import com.drama.train.ticket.service.http.context.IHttpContext;
import com.drama.train.ticket.service.http.cookie.DefaultHttpCookie;
import com.drama.train.ticket.service.http.cookie.IHttpCookie;
import com.drama.train.ticket.service.http.header.DefaultHttpHeader;
import com.drama.train.ticket.service.http.header.IHttpHeader;
import com.drama.train.ticket.service.http.parameter.EntityParameter;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import com.drama.train.ticket.service.http.parameter.IHttpParameter;
import com.drama.train.ticket.service.http.request.HttpPostRequest;
import com.drama.train.ticket.service.http.request.IHttpRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Objects;

@Log4j2
public abstract class AbstractHttpAction implements IHttpAction {
    protected final Http12306Config http12306Config;

    protected AbstractHttpAction(Http12306Config http12306Config) {
        this.http12306Config = http12306Config;
    }

    protected HttpResult doRequest(HttpClientContext httpContext, CookieStore cookieStore, HttpParameter parameters){
        URI url = prepareUrl(parameters);
        if(url==null)
            return new HttpResult(null, httpContext, cookieStore, null, "")
                    .setResultSendFail("无法生成URL");
        long begin = System.currentTimeMillis();
        IHttpRequest httpRequest = prepareHttpRequest();
        IHttpHeader httpHeader = prepareHttpHeader();
        IHttpParameter httpParameter = prepareHttpParameter();
        IHttpCookie httpCookie = prepareHttpCookie();
        IHttpContext iHttpContext = prepareHttpContext();

        cookieStore = httpCookie.createCookieStore(cookieStore);                    //设置cookie
        CloseableHttpClient httpClient=createHttpClientAndSetCookie(cookieStore);   //根据需求创建带cookie的httpClient
        HttpRequestBase httpRequestBase = httpRequest.getHttpRequest(url);          //根据需要创建get请求或post请求
        List<NameValuePair> headers = httpHeader.getHeader();                       //获取header变量
        HttpEntity parameter = httpParameter.getParameter(parameters);              //把参数转换成httpEntity对象
        httpContext = iHttpContext.creatHttpContext(httpContext);

        if(headers!=null)
            headers.stream().filter(Objects::nonNull).forEach(h->httpRequestBase.setHeader(h.getName(),h.getValue()));

        if(httpRequestBase instanceof HttpEntityEnclosingRequestBase){  //如果是post请求，需要设置form-data参数
            HttpEntityEnclosingRequestBase requestBase = (HttpEntityEnclosingRequestBase) httpRequestBase;
            if(parameter!=null)
                requestBase.setEntity(parameter);
        }

        CloseableHttpResponse httpResponse=null;
        InputStream inputStream=null;
        HttpResult httpResult;
        try {
            httpResponse=httpClient.execute(httpRequestBase, httpContext);      //发送请求，获取结果
            StatusLine responseStatusLine = httpResponse.getStatusLine();
            if(responseStatusLine.getStatusCode()!=200) {                       //请求异常或失败
                httpResult = new HttpResult(url, httpContext, cookieStore, responseStatusLine, "")
                                    .setResultFail(url+" 请求返回错误码"+responseStatusLine.getStatusCode());
                log.warn("请求返回异常:"+httpResult);
                return httpResult;
            }
            HttpEntity httpEntity=httpResponse.getEntity();
            inputStream=httpEntity.getContent(); //获取content实体内容
            String responseText = IOUtils.toString(inputStream,"UTF-8");
            httpResult = new HttpResult(url, httpContext, cookieStore, responseStatusLine, responseText)
                                .setResultOk("请求成功返回，结果还未解析");
            long end = System.currentTimeMillis();
            log.info("发起请求,耗时："+(end-begin)+"ms，url:" + url);
            return httpResult;
        } catch (Exception e) {
            log.error("请求错误:"+e.getMessage(), e);
            httpResult = new HttpResult(url, httpContext, cookieStore,null, null)
                                    .setResultSendFail(e.getMessage());
        }finally {
            if(inputStream!=null){
                try{inputStream.close();}catch (Exception ignored){}
            }
            if(httpResponse!=null){
                try{httpResponse.close();}catch (Exception ignored){}
            }
        }
        return httpResult;
    }

    private CloseableHttpClient createHttpClientAndSetCookie(CookieStore cookieStore){
        CloseableHttpClient httpClient;
        if(cookieStore!=null) {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            httpClient = httpClientBuilder.setDefaultCookieStore(cookieStore).build();
        }else
            httpClient = HttpClients.createDefault();
        return httpClient;
    }

    @Override
    public IHttpContext prepareHttpContext(){
        return new DefaultHttpContext();
    }

    @Override
    public IHttpParameter prepareHttpParameter() {
        return new EntityParameter();
    }

    @Override
    public IHttpCookie prepareHttpCookie() {
        return new DefaultHttpCookie();
    }

    @Override
    public IHttpHeader prepareHttpHeader() {
        return new DefaultHttpHeader(http12306Config);
    }

    @Override
    public IHttpRequest prepareHttpRequest() {
        return new HttpPostRequest();
    }
}
