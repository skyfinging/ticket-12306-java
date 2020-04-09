package com.drama.train.ticket.config;

import com.drama.train.ticket.service.StationNameService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 购票信息配置
 */
@Configuration
@PropertySource(value={"classpath:buyTicket.properties"}, encoding="UTF-8")
@Getter
public class BuyTicketConfig {

    @Autowired
    StationNameService stationNameService;

    @Value("${train_date}")
    private String trainDate;

    @Value("${from_station}")
    private String fromStation;

    @Value("${to_station}")
    private String toStation;

    @Value("${purpose_codes}")
    private String purposeCodes;

    @Value("${train.code}")
    private String trainCode;

    @Value("${tour_flag}")
    private String tourFlag;

    @Value("${passenger}")
    private String passenger;

    @Value("${seat.type}")
    private String seatType;

    @Value("${choose.seat}")
    private String chooseSeat;

    @Value("${notice.mail}")
    private String noticeMail;

    public String getFromStationCode(){
        return stationNameService.getStationCode(fromStation);
    }

    public String getToStationCode(){
        return stationNameService.getStationCode(toStation);
    }
}
