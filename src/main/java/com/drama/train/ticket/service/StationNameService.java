package com.drama.train.ticket.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 把车站名称和对应的英文对应起来，放在缓存中
 */
@Service
@Log4j2
public class StationNameService {
    @Value("${station_file}")
    private String stationFileName;

    private Map<String, String> cache = new HashMap<>();

    /**
     * 根据车站名称获取车站代号
     * @param stationName
     * @return
     */
    public String getStationCode(String stationName){
        if(cache.isEmpty()){
            init();
        }
        String code = cache.get(stationName);
        return code;
    }

    /**
     * 根据车站代号获取车站名称
     * @param stationCode
     * @return
     */
    public String getStationName(String stationCode){
        if(cache.isEmpty()){
            init();
        }
        Iterator<String> iterator = cache.keySet().iterator();
        while(iterator.hasNext()){
            String name = iterator.next();
            String code = cache.get(name);
            if(code!=null && code.equals(stationCode)){
                return name;
            }
        }
        return "";
    }

    private void init(){
        ClassPathResource resource = new ClassPathResource(stationFileName);
        try(InputStream inputStream = resource.getInputStream()){
            String str = IOUtils.toString(inputStream, "UTF-8");
            if(str!=null) {
                String[] stations = str.split("@");
                Arrays.asList(stations).forEach(this::handleStationStr);
            }
        } catch (IOException e) {
            log.error("读取车站文件失败:"+e.getMessage(), e);
        }
    }

    /**
     * 处理车站信息字符串
     * bjb|北京北|VAP|beijingbei|bjb|0
     * 从中获取"北京北"-"VAP"对应关系
     * @param station
     */
    private void handleStationStr(String station){
        if(station==null || station.isEmpty())
            return;
        String[] split = station.split("\\|");
        if(split.length<3)
            return;
        cache.put(split[1],split[2]);
    }
}
