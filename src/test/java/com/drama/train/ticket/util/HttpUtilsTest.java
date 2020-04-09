package com.drama.train.ticket.util;

import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.CheckQREntity;
import com.drama.train.ticket.entity.QRCodeEntity;
import com.drama.train.ticket.service.HttpService;
import com.drama.train.ticket.service.http.*;
import com.drama.train.ticket.service.http.parameter.HttpParameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HttpUtilsTest {

    @Autowired
    Http12306Config http12306Config;

    @Autowired
    HttpService httpService;


    Http12306CreateQrAction createQrAction = new Http12306CreateQrAction(http12306Config, httpService);
    Http12306ConfAction http12306ConfAction = new Http12306ConfAction(http12306Config);
    Http12306BannerAction bannerAction = new Http12306BannerAction(http12306Config);
    Http12306UamtkAction uamtkAction = new Http12306UamtkAction(http12306Config,httpService);
    Http12306CheckQrAction checkQrAction = new Http12306CheckQrAction(http12306Config, httpService);

//    @Test
    public void testdoGet() throws URISyntaxException, JsonProcessingException {
        HttpResult httpResult = createQrAction.action(null, null,null);
        String result = httpResult.getResponseText();
        QRCodeEntity qrCodeEntity = Http12306CreateQrAction.parseQRCodeEntity(result);
        System.out.println(result);
        assertEquals(httpResult.getUrl().getPath(),http12306Config.getCreateQrUrl());
        assertEquals(httpResult.getResponseStatusLine().getStatusCode(), 200);
        assertEquals(Integer.valueOf(0), qrCodeEntity.getResult_code());
        assertEquals("生成二维码成功", qrCodeEntity.getResult_message());
        assertNotNull(qrCodeEntity.getImage());
        assertNotNull(qrCodeEntity.getUuid());

    }

//    @Test
    public void testdoPost() throws JsonProcessingException, InterruptedException {
        HttpResult httpResult = createQrAction.action(null, null,null);
        String result = httpResult.getResponseText();
        QRCodeEntity qrCodeEntity = Http12306CreateQrAction.parseQRCodeEntity(result);
        System.out.println("data:image/jpg;base64,"+qrCodeEntity.getImage());
        System.out.println(qrCodeEntity.getUuid());
        String uuid = qrCodeEntity.getUuid();
        HttpClientContext context = httpResult.getHttpContext();
        CookieStore cookieStore = httpResult.getCookieStore();

        //请求cookie
        http12306ConfAction.action(context, cookieStore, null);
        bannerAction.action(context, cookieStore,  null);
        uamtkAction.action(context, cookieStore,  null);

        ActionParameter actionParameter = ActionParameter.of().addParameter(checkQrAction.NAME_PARAM_UUID, uuid);
        while(true) {
            httpResult = checkQrAction.action(context, cookieStore, actionParameter);
            result = httpResult.getResponseText();
            CheckQREntity checkQREntity = (CheckQREntity) httpResult.getEntity();
            System.out.println(result);
            if(checkQREntity.getResult_code()==3)
                break;
            Thread.sleep(10000);
        }
    }
}