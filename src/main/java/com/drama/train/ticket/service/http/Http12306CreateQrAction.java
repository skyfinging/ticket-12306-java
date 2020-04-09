package com.drama.train.ticket.service.http;

import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.QRCodeEntity;
import com.drama.train.ticket.service.HttpService;
import com.drama.train.ticket.service.http.context.CreateHttpContext;
import com.drama.train.ticket.service.http.context.IHttpContext;
import com.drama.train.ticket.service.http.cookie.DeviceHttpCookie;
import com.drama.train.ticket.service.http.cookie.IHttpCookie;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import com.drama.train.ticket.service.http.request.HttpGetRequest;
import com.drama.train.ticket.service.http.request.IHttpRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Log4j2
public class Http12306CreateQrAction extends AbstractHttpAction {
    final HttpService httpService;

    public Http12306CreateQrAction(Http12306Config http12306Config,HttpService httpService) {
        super(http12306Config);
        this.httpService = httpService;
    }

    /**
     *
     * @param httpParameter 1个参数appid
     * @return
     */
    @Override
    public URI prepareUrl(HttpParameter httpParameter) {
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(http12306Config.getScheme())
                    .setHost(http12306Config.getHost())
                    .setPath(http12306Config.getCreateQrUrl());
            if(httpParameter ==null)
                return uriBuilder.build();
            return httpParameter.setParameterToURL(uriBuilder).build();
        } catch (URISyntaxException e) {
            log.error("无法获取二维码url："+e.getMessage(), e);
        }
        return null;
    }

    public static QRCodeEntity parseQRCodeEntity(String json) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
        QRCodeEntity qrCodeEntity = null;
        qrCodeEntity = mapper.readValue(json, QRCodeEntity.class);
        return qrCodeEntity;
    }

    @Override
    public IHttpRequest prepareHttpRequest() {
        return new HttpGetRequest();
    }

    @Override
    public IHttpCookie prepareHttpCookie() {
        return new DeviceHttpCookie(httpService);
    }

    @Override
    public IHttpContext prepareHttpContext(){
        return new CreateHttpContext();
    }

    @Override
    public HttpResult action(HttpClientContext context, CookieStore cookieStore, ActionParameter actionParameter) {
        HttpParameter httpParameter = HttpParameter.of().addParameter("appid",httpService.getAppId());
        HttpResult httpResult = doRequest(null, null, httpParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.HTTP_OK){
            String msg = "无法获取二维码";
            log.info(msg);
            return httpResult;
        }
        try {
            QRCodeEntity qrCodeEntity = parseQRCodeEntity(httpResult.getResponseText());
            httpResult.setResultResponseOK(qrCodeEntity);
            return httpResult;
        }catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            log.error(httpResult.getResponseText());
            httpResult.setResultSendFail("无法解析二维码请求结果,"+e.getMessage());
            return httpResult;
        }
    }
}
