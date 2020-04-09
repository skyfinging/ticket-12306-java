package com.drama.train.ticket.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class PassengerDtoDataEntity{
    private boolean isExist;
    private List<PassengerEntity> normal_passengers;
}
