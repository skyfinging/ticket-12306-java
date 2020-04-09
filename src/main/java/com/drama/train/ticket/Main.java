package com.drama.train.ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 提供12306抢票功能，仅能抢平时的火车票，刚发售的火车票无法抢，因为刚发售的票需要滑动验证码，这个目前破解不了.
 * 购票完成之后可以发送邮件通知。
 * 使用之前需要配置购票信息、乘车人等
 * 如果要使用邮件通知功能，需要配置邮件服务器信息
 *
 * 注意：
 * 1.每隔一段时间，12306的余票查询url就换一个，如果/buy请求出现异常，请检查一下12306的余票查询url是什么
 * 2.如果扫码登陆失败，提示未扫码，则说明机器码变了，需要改配置文件中的机器码
 *
 * 使用说明：
 * 1.程序启动后，在浏览器中输入Http://127.0.0.1:8080/login ，然后用12306App扫码登陆
 * 2.扫码成功后，在浏览器中输入Http://127.0.0.1:8080/index，看看是否登陆成功
 * 3.登陆成功之后，如果开启了自动刷票抢票，则完成，如果没有开启自动刷票抢票，则在浏览器中输入Http://127.0.0.1:8080/buy，手动触发购票
 */
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
