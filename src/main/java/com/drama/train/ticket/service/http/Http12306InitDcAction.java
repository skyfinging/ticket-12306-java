package com.drama.train.ticket.service.http;

import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.InitDcEntity;
import com.drama.train.ticket.service.http.header.IHttpHeader;
import com.drama.train.ticket.service.http.header.TicketHttpHeader;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class Http12306InitDcAction extends AbstractHttpAction {

    public Http12306InitDcAction(Http12306Config http12306Config) {
        super(http12306Config);
    }

    /**
     *
     * @param httpParameter 1个参数,不需要设置参数值
     *   _json_att=
     * @return
     */
    @Override
    public URI prepareUrl(HttpParameter httpParameter) {
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(http12306Config.getScheme())
                    .setHost(http12306Config.getHost())
                    .setPath(http12306Config.getInitDcUrl());
            if(httpParameter ==null)
                return uriBuilder.build();
            return httpParameter.setParameterToURL(uriBuilder).build();
        } catch (URISyntaxException e) {
            log.error("无法获取url："+e.getMessage(), e);
        }
        return null;
    }

    public static String getGlobalRepeatSubmitToken(String responseText){
        Pattern pattern = Pattern.compile("var\\s+globalRepeatSubmitToken\\s*=\\s*'([0-9a-zA-Z]{32})'");
        Matcher matcher = pattern.matcher(responseText);
        if(matcher.find()){
            return matcher.group(1);
        }
        return "";
    }

    public static String getLeftTicket(String responseText){
        Pattern pattern = Pattern.compile("'leftTicketStr':'([0-9A-Za-z%]+)'");
        Matcher matcher = pattern.matcher(responseText);
        if(matcher.find()){
            return matcher.group(1);
        }
        return "";
    }

    public static String getPurposeCodes(String responseText){
        Pattern pattern = Pattern.compile("'purpose_codes':'([0-9A-Za-z%]+)'");
        Matcher matcher = pattern.matcher(responseText);
        if(matcher.find()){
            return matcher.group(1);
        }
        return "";
    }

    public static String getTrainLocation(String responseText){
        Pattern pattern = Pattern.compile("'train_location':'([0-9A-Za-z%]+)'");
        Matcher matcher = pattern.matcher(responseText);
        if(matcher.find()){
            return matcher.group(1);
        }
        return "";
    }

    public static String getKeyCheckIsChange(String responseText){
        Pattern pattern = Pattern.compile("'key_check_isChange':'([0-9A-Za-z%]+)'");
        Matcher matcher = pattern.matcher(responseText);
        if(matcher.find()){
            return matcher.group(1);
        }
        return "";
    }

    @Override
    public IHttpHeader prepareHttpHeader() {
        return new TicketHttpHeader(http12306Config);
    }

    @Override
    public HttpResult action(HttpClientContext context, CookieStore cookieStore, ActionParameter actionParameter) {
        HttpParameter httpParameter = HttpParameter.of().addParameter("_json_att","");
        HttpResult httpResult = doRequest(context, cookieStore, httpParameter);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.HTTP_OK){
            log.info("无法初始化单程票");
            return httpResult;
        }
        String globalRepeatSubmitToken = Http12306InitDcAction.getGlobalRepeatSubmitToken(httpResult.getResponseText());
        String leftTicket = Http12306InitDcAction.getLeftTicket(httpResult.getResponseText());
        String purposeCodes = Http12306InitDcAction.getPurposeCodes(httpResult.getResponseText());
        String trainLocation = Http12306InitDcAction.getTrainLocation(httpResult.getResponseText());
        String keyCheckIsChange = Http12306InitDcAction.getKeyCheckIsChange(httpResult.getResponseText());
        if(globalRepeatSubmitToken==null || leftTicket==null || purposeCodes==null || trainLocation==null || keyCheckIsChange==null){
            log.info("单程票信息缺失,"+httpResult.getResponseText());
            httpResult.setResultInterrupt("单程票信息缺失,"+httpResult.getResponseText());
            return httpResult;
        }

        InitDcEntity initDcEntity = new InitDcEntity();
        initDcEntity.setGlobalRepeatSubmitToken(globalRepeatSubmitToken);
        initDcEntity.setLeftTicket(leftTicket);
        initDcEntity.setPurposeCodes(purposeCodes);
        initDcEntity.setTrainLocation(trainLocation);
        initDcEntity.setKeyCheckIsChange(keyCheckIsChange);
        httpResult.setResultResponseOK(initDcEntity);
        return httpResult;
    }
}
