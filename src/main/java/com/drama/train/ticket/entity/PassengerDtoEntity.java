package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PassengerDtoEntity {
    private boolean status;
    private Integer httpstatus;
    private PassengerDtoDataEntity data;

    public PassengerEntity getPassenger(String name){
        if(name==null)
            return null;
        if(data==null || data.getNormal_passengers()==null)
            return null;
        for(PassengerEntity passenger : data.getNormal_passengers()){
            if(passenger==null)
                continue;
            if(name.equals(passenger.getPassenger_name()))
                return passenger;
        }
        return null;
    }


}




