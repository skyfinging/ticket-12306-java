package com.drama.train.ticket.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 12306的余票信息，一趟列车一个TrainInfo
 */
@Getter
@Setter
public class TrainInfo {
    private String secretStr;   //在提交/otn/leftTicket/submitOrderRequest的时候需要这个信息
    private String buttonTextInfo;  //固定字符串"预订"
    private String trainNo;        //车票号，在提交/otn/confirmPassenger/getQueueCount的时候需要这个信息
    private String trainCode;   //车次
    private String beginStation;//起点站
    private String endStation;  //终点站
    private String boardStation;//出发站
    private String arriveStation;  //到达站
    private String beginTime;   //出发时间HH:mm
    private String arriveTime;  //到达时间HH:mm
    private String time;        //历时时长HH;mm
    private String canWebBuy;   //是否可以购买,Y/N
    private String leftTicketStr;   //在提交/otn/confirmPassenger/confirmSingleForQueue的时候需要这个信息
    private String ticketDate;  //出发日期yyyyMMdd
    private String trainSeatFeature;    //意义不明
    private String trainLocation;   //在提交/otn/confirmPassenger/confirmSingleForQueue的时候需要这个信息
    private String boardStationNo;  //该车次的出发站序号，下标从1开始，起点站的序号是1
    private String arriveStationNo; //该车次的达到站序号
    private String isSupportCard;   //是否支持二代身份证进站，1/0
    private String controlledTrainFlag; //意义不明
    //以下为席位，格式包括数字、有、无、空
    private String ggNum;
    private String gjrwNum;     //高级软卧
    private String qtNum;
    private String rwNum;       //软卧
    private String rzNum;       //软座
    private String tzNum;
    private String wzNum;       //无座，当无座的值为无的时候，没有候补
    private String ybNum;
    private String ywNum;       //硬卧
    private String yzNum;       //硬座
    private String edzNum;      //二等座
    private String ydzNum;      //一等座
    private String swzNum;      //商务座
    private String dwNum;       //动卧

    //最后四个字段意义不明
    private String str35;
    private String str36;
    private String str37;
    private String str38;

    @Override
    public String toString(){
        return "车次:"+trainCode+"，是否可以预订："+canWebBuy+"，高级软卧："+gjrwNum+"，软卧："+rwNum+"，软座："+rzNum+"，无座："+wzNum+"，硬卧："+ywNum+"，硬座："+yzNum+"，二等座："+edzNum+"，一等座："+ydzNum+"，商务座："+swzNum+"，动卧："+dwNum;
    }

    public TrainInfo(String str){
        if(str!=null){
            String[] split = str.split("\\|");
            if(split.length>=38){
                secretStr = split[0];
                buttonTextInfo = split[1];
                trainNo = split[2];
                trainCode = split[3];
                beginStation = split[4];
                endStation = split[5];
                boardStation = split[6];
                arriveStation = split[7];
                beginTime = split[8];
                arriveTime = split[9];
                time = split[10];
                canWebBuy = split[11];
                leftTicketStr = split[12];
                ticketDate = split[13];
                trainSeatFeature = split[14];
                trainLocation = split[15];
                boardStationNo = split[16];
                arriveStationNo = split[17];
                isSupportCard = split[18];
                controlledTrainFlag = split[19];
                ggNum = split[20];
                gjrwNum = split[21];
                qtNum = split[22];
                rwNum = split[23];
                rzNum = split[24];
                tzNum = split[25];
                wzNum = split[26];
                ybNum = split[27];
                ywNum = split[28];
                yzNum = split[29];
                edzNum = split[30];
                ydzNum = split[31];
                swzNum = split[32];
                dwNum = split[33];
                str35 = split[34];
                str36 = split[35];
                str37 = split[36];
                str38 = split[37];
            }
        }
    }

    public String getSeatNum(String seatName){
        if ("二等座".equals(seatName)) {
            return edzNum;
        }else if("一等座".equals(seatName)){
            return ydzNum;
        }else if("商务座".equals(seatName)){
            return swzNum;
        }else if("动卧".equals(seatName))
            return dwNum;
        else if("高级软卧".equals(seatName))
            return gjrwNum;
        else if("软卧".equals(seatName))
            return rwNum;
        else if("软座".equals(seatName))
            return rzNum;
        else if("无座".equals(seatName))
            return wzNum;
        else if("硬卧".equals(seatName))
            return ywNum;
        else if("硬座".equals(seatName))
            return yzNum;
        return "";
    }
}
