package com.drama.train.ticket.service.http;

import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.AuthUamtkEntity;
import com.drama.train.ticket.service.HttpService;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Log4j2
public class Http12306AuthUamtkAction extends AbstractHttpAction {

    private final HttpService httpService;
    public Http12306AuthUamtkAction(Http12306Config http12306Config, HttpService httpService) {
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
                    .setPath(http12306Config.getAuthUamtkUrl());
            if(httpParameter ==null)
                return uriBuilder.build();
            return httpParameter.setParameterToURL(uriBuilder).build();
        } catch (URISyntaxException e) {
            log.error("无法获取url："+e.getMessage(), e);
        }
        return null;
    }

    @Override
    public HttpResult action(HttpClientContext context, CookieStore cookieStore, ActionParameter actionParameter) {
        HttpParameter httpParameter = HttpParameter.of().addParameter("appid",httpService.getAppId());
        HttpResult httpResult = doRequest(context, cookieStore, httpParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.HTTP_OK){
            log.info("AuthUamtk请求失败");
            return httpResult;
        }
        try {
            AuthUamtkEntity authUamtkEntity = Http12306AuthUamtkAction.parseAuthUamtkEntity(httpResult.getResponseText());
            if(authUamtkEntity.getResult_code()==1){
                log.info("AuthUamtk请求失败:"+authUamtkEntity);
                httpResult.setResultResponseError(authUamtkEntity.getResult_message());
                return httpResult;
            }
            httpResult.setResultResponseOK(authUamtkEntity);
            return httpResult;
        }catch (JsonProcessingException e) {
            log.error(httpResult.getResponseText());
            log.error(e.getMessage(), e);
            httpResult.setResultSendFail("无法解析AuthUamtk的结果,"+e.getMessage());
            return httpResult;
        }
    }

    public static AuthUamtkEntity parseAuthUamtkEntity(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        AuthUamtkEntity authUamtkEntity = null;
        authUamtkEntity = mapper.readValue(json, AuthUamtkEntity.class);
        return authUamtkEntity;
    }

}
