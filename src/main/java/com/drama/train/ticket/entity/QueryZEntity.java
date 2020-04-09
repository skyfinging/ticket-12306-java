package com.drama.train.ticket.entity;

import com.drama.train.ticket.bean.TrainInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class QueryZEntity {
    private Integer httpstatus;
    private QueryZDataEntity data;
    private String messages;
    private boolean status;

    public TrainInfo getTrainInfo(String trainCode){
        if(data==null || trainCode==null)
            return null;
        for (String str : data.getResult()){
            TrainInfo trainInfo = new TrainInfo(str);
            if(trainCode.equalsIgnoreCase(trainInfo.getTrainCode())){
                return trainInfo;
            }
        }
        return null;
    }
}

@Getter
@Setter
@ToString
class QueryZDataEntity{
    private List<String> result;
    private String flag;
    private Map<String, String> map;
}
