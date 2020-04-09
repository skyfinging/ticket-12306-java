package com.drama.train.ticket.service.http;

import com.drama.train.ticket.entity.PassengerDtoEntity;
import com.drama.train.ticket.entity.PassengerEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static org.junit.Assert.*;

public class Http12306PassengerDtoActionTest {

    @Test
    public void parsePassengerDtoEntity() throws JsonProcessingException {
        String text = "{\"validateMessagesShowId\":\"_validatorMessage\",\"status\":true,\"httpstatus\":200,\"data\":{\"notify_for_gat\":\"\",\"isExist\":true,\"exMsg\":\"\",\"two_isOpenClick\":[\"93\",\"95\",\"97\",\"99\"],\"other_isOpenClick\":[\"91\",\"93\",\"98\",\"99\",\"95\",\"97\"],\"normal_passengers\":[{\"passenger_name\":\"陈锐均\",\"sex_code\":\"M\",\"sex_name\":\"男\",\"born_date\":\"1992-08-02 00:00:00\",\"country_code\":\"CN\",\"passenger_id_type_code\":\"1\",\"passenger_id_type_name\":\"中国居民身份证\",\"passenger_id_no\":\"4451***********650\",\"passenger_type\":\"1\",\"passenger_flag\":\"0\",\"passenger_type_name\":\"成人\",\"mobile_no\":\"18664844539\",\"phone_no\":\"\",\"email\":\"250805603@qq.com\",\"address\":\"\",\"postalcode\":\"\",\"first_letter\":\"\",\"recordCount\":\"7\",\"total_times\":\"99\",\"index_id\":\"0\",\"allEncStr\":\"216c20718a316cc5de0ed7027a18b9151425eab660a56e8024888b4773a7f3ab8cb5c6a6e3ccb6de39b8ce423fe63948\",\"isAdult\":\"Y\",\"isYongThan10\":\"N\",\"isYongThan14\":\"N\",\"isOldThan60\":\"N\",\"gat_born_date\":\"\",\"gat_valid_date_start\":\"\",\"gat_valid_date_end\":\"\",\"gat_version\":\"\"},{\"passenger_name\":\"苏时奋\",\"sex_code\":\"M\",\"sex_name\":\"男\",\"born_date\":\"2008-01-01 00:00:00\",\"country_code\":\"CN\",\"passenger_id_type_code\":\"1\",\"passenger_id_type_name\":\"中国居民身份证\",\"passenger_id_no\":\"4503***********028\",\"passenger_type\":\"1\",\"passenger_flag\":\"0\",\"passenger_type_name\":\"成人\",\"mobile_no\":\"\",\"phone_no\":\"\",\"email\":\"\",\"address\":\"\",\"postalcode\":\"\",\"first_letter\":\"SSF\",\"recordCount\":\"7\",\"total_times\":\"99\",\"index_id\":\"1\",\"allEncStr\":\"6665ccee3adf9e9c5d0a49d37ad4d94f113fec0639bac63ba00092e934b41a81\",\"isAdult\":\"Y\",\"isYongThan10\":\"N\",\"isYongThan14\":\"N\",\"isOldThan60\":\"N\",\"gat_born_date\":\"\",\"gat_valid_date_start\":\"\",\"gat_valid_date_end\":\"\",\"gat_version\":\"\"},{\"passenger_name\":\"王丽媛\",\"sex_code\":\"F\",\"sex_name\":\"女\",\"born_date\":\"2015-12-05 00:00:00\",\"country_code\":\"CN\",\"passenger_id_type_code\":\"1\",\"passenger_id_type_name\":\"中国居民身份证\",\"passenger_id_no\":\"3707***********006\",\"passenger_type\":\"1\",\"passenger_flag\":\"0\",\"passenger_type_name\":\"成人\",\"mobile_no\":\"13826070590\",\"phone_no\":\"\",\"email\":\"\",\"address\":\"\",\"postalcode\":\"\",\"first_letter\":\"WLY\",\"recordCount\":\"7\",\"total_times\":\"99\",\"index_id\":\"2\",\"allEncStr\":\"02f1b9567b409361eb9bead8725d6833cb0688ec958e55ade7a1310c6f8b601ea32b56f340eaa4bc4b0fda5da2ae7168\",\"isAdult\":\"Y\",\"isYongThan10\":\"N\",\"isYongThan14\":\"N\",\"isOldThan60\":\"N\",\"gat_born_date\":\"\",\"gat_valid_date_start\":\"\",\"gat_valid_date_end\":\"\",\"gat_version\":\"\"},{\"passenger_name\":\"韦平忠\",\"sex_code\":\"M\",\"sex_name\":\"男\",\"born_date\":\"2014-12-10 00:00:00\",\"country_code\":\"CN\",\"passenger_id_type_code\":\"1\",\"passenger_id_type_name\":\"中国居民身份证\",\"passenger_id_no\":\"4521***********611\",\"passenger_type\":\"3\",\"passenger_flag\":\"0\",\"passenger_type_name\":\"学生\",\"mobile_no\":\"\",\"phone_no\":\"\",\"email\":\"\",\"address\":\"\",\"postalcode\":\"\",\"first_letter\":\"WPZ\",\"recordCount\":\"7\",\"total_times\":\"99\",\"index_id\":\"3\",\"allEncStr\":\"fb441f662f65e23a44211dcd2403577632447e7ef189cdb93292adf345f21101\",\"isAdult\":\"Y\",\"isYongThan10\":\"N\",\"isYongThan14\":\"N\",\"isOldThan60\":\"N\",\"gat_born_date\":\"\",\"gat_valid_date_start\":\"\",\"gat_valid_date_end\":\"\",\"gat_version\":\"\"},{\"passenger_name\":\"肖大东\",\"sex_code\":\"M\",\"sex_name\":\"男\",\"born_date\":\"2015-01-03 00:00:00\",\"country_code\":\"CN\",\"passenger_id_type_code\":\"1\",\"passenger_id_type_name\":\"中国居民身份证\",\"passenger_id_no\":\"3607***********219\",\"passenger_type\":\"3\",\"passenger_flag\":\"0\",\"passenger_type_name\":\"学生\",\"mobile_no\":\"\",\"phone_no\":\"\",\"email\":\"\",\"address\":\"\",\"postalcode\":\"\",\"first_letter\":\"XDD\",\"recordCount\":\"7\",\"total_times\":\"99\",\"index_id\":\"4\",\"allEncStr\":\"200c92bf40fc1cbb256db9865941962b8355e2a81550da88e40b70e7972622da\",\"isAdult\":\"Y\",\"isYongThan10\":\"N\",\"isYongThan14\":\"N\",\"isOldThan60\":\"N\",\"gat_born_date\":\"\",\"gat_valid_date_start\":\"\",\"gat_valid_date_end\":\"\",\"gat_version\":\"\"},{\"passenger_name\":\"熊天成\",\"sex_code\":\"M\",\"sex_name\":\"男\",\"born_date\":\"1992-01-17 00:00:00\",\"country_code\":\"CN\",\"passenger_id_type_code\":\"1\",\"passenger_id_type_name\":\"中国居民身份证\",\"passenger_id_no\":\"4522***********019\",\"passenger_type\":\"1\",\"passenger_flag\":\"0\",\"passenger_type_name\":\"成人\",\"mobile_no\":\"\",\"phone_no\":\"\",\"email\":\"\",\"address\":\"\",\"postalcode\":\"\",\"first_letter\":\"XTC\",\"recordCount\":\"7\",\"total_times\":\"99\",\"index_id\":\"5\",\"allEncStr\":\"f340d76733a6b7ae81878ff6523d6bb51953235a824e911b07e701ef1f587bc6\",\"isAdult\":\"Y\",\"isYongThan10\":\"N\",\"isYongThan14\":\"N\",\"isOldThan60\":\"N\",\"gat_born_date\":\"\",\"gat_valid_date_start\":\"\",\"gat_valid_date_end\":\"\",\"gat_version\":\"\"},{\"passenger_name\":\"张春创\",\"sex_code\":\"M\",\"sex_name\":\"男\",\"born_date\":\"2008-01-01 00:00:00\",\"country_code\":\"CN\",\"passenger_id_type_code\":\"1\",\"passenger_id_type_name\":\"中国居民身份证\",\"passenger_id_no\":\"4503***********078\",\"passenger_type\":\"1\",\"passenger_flag\":\"0\",\"passenger_type_name\":\"成人\",\"mobile_no\":\"\",\"phone_no\":\"\",\"email\":\"\",\"address\":\"\",\"postalcode\":\"\",\"first_letter\":\"ZCC\",\"recordCount\":\"7\",\"total_times\":\"99\",\"index_id\":\"6\",\"allEncStr\":\"1e4e5ac6a9b87eeff4283bf85120a640be3a87d0eb1ff5bc2ed1f1c97e5e9e4a\",\"isAdult\":\"Y\",\"isYongThan10\":\"N\",\"isYongThan14\":\"N\",\"isOldThan60\":\"N\",\"gat_born_date\":\"\",\"gat_valid_date_start\":\"\",\"gat_valid_date_end\":\"\",\"gat_version\":\"\"}],\"dj_passengers\":[]},\"messages\":[],\"validateMessages\":{}}";
        PassengerDtoEntity entity = Http12306PassengerDtoAction.parsePassengerDtoEntity(text);
        PassengerEntity passengerEntity = entity.getPassenger("陈锐均");
        System.out.println(passengerEntity);
        assertEquals("陈锐均",passengerEntity.getPassenger_name());
    }
}