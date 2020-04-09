package com.drama.train.ticket.controller;

import com.drama.train.ticket.config.Http12306Config;
import com.drama.train.ticket.entity.QRCodeEntity;
import com.drama.train.ticket.service.HttpService;
import com.drama.train.ticket.service.http.*;

import lombok.extern.log4j.Log4j2;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@Log4j2
public class LoginController {

    @Autowired
    Http12306Config http12306Config;

    @Autowired
    HttpService httpService;


    /**
     * 使用12306手机APP进行完成扫描登陆，之后才能购票
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/login")
    public String login(HttpServletRequest request, Model model){
        Http12306CreateQrAction createQrAction = new Http12306CreateQrAction(http12306Config, httpService);
        Http12306ConfAction http12306ConfAction = new Http12306ConfAction(http12306Config);
        Http12306BannerAction bannerAction = new Http12306BannerAction(http12306Config);
        Http12306UamtkAction uamtkAction = new Http12306UamtkAction(http12306Config, httpService);

        //请求二维码
        HttpResult httpResult = createQrAction.action(null ,null, null);
        if(httpResult.getResultStatus()!=HttpResultStatusEnum.RESPONSE_OK){
            model.addAttribute("codeError",true);
            return "loginPage";
        }
        QRCodeEntity qrCodeEntity = (QRCodeEntity) httpResult.getEntity();
        String qrImg = "data:image/jpg;base64,"+qrCodeEntity.getImage();        //真实二维码的url
        boolean codeError = qrCodeEntity.getResult_code()==0?false:true;
        String uuid = qrCodeEntity.getUuid();

        //绑定二维码的UUID，cookie等
        HttpService.setUUid(request, uuid);
        HttpClientContext context = httpResult.getHttpContext();
        CookieStore cookieStore = httpResult.getCookieStore();
        httpService.putContext(uuid, context);
        httpService.putCookieStore(uuid, cookieStore);

        //模仿浏览器完成三个请求conf、banner、uamtk
        http12306ConfAction.action(context, cookieStore, null);
        bannerAction.action(context, cookieStore, null);
        uamtkAction.action(context, cookieStore, null);

        model.addAttribute("qrImg",qrImg);
        model.addAttribute("codeError",codeError);

        return "loginPage";
    }

    /**
     * 二维码状态，如果超过一定时间不扫描二维码，二维码会失效，需要刷新页面重新请求二维码
     * 这个可以不用管，如果手机扫描失败，刷新页面重新扫描就行
     * @param request
     * @return
     */
    @RequestMapping("/checkqr")
    @ResponseBody
    public String checkqr(HttpServletRequest request){
//        String uuid = HttpService.getUUid(request);
//        HttpClientContext context = httpService.getContext(uuid);
//        CookieStore cookieStore = httpService.getCookieStore(uuid);
//        System.out.println("检查二维码是否有效:"+uuid);
//        HttpParameter httpParameter = HttpParameter.of().addParameter("uuid",uuid).addParameter("appid",httpService.getAppId());
//        HttpResult httpResult = checkQrAction.doRequest(context, cookieStore, uuid, httpParameter);
//        if(httpResult.getStatusCode()==200){
//            return httpResult.getResponseText();
//        }

        return "";
    }

}
