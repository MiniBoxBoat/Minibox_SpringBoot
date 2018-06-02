package com.minibox.dao.db;

import com.minibox.po.Transportation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TransportationMapperTest {

    @Resource
    private TransportationMapper transportationMapper;
    private Transportation transportation;

    @Before
    public void before(){
        transportation = Transportation.builder()
                .transportationId(1)
                .userId(1)
                .startPlace("重庆")
                .endPlace("上海")
                .name("May")
                .phoneNumber("15808060138")
                .receiveTime("2018-08-01")
                .goodsType("衣服")
                .company("韵达")
                .transportationComment("尽快")
                .build();
    }

    @Test
    public void insertTransportationTest(){
        boolean flag = transportationMapper.insertTransportation(transportation);
        assertEquals(true, flag);
    }

    @Test
    public void updateTransportationTest(){
        transportation.setStartPlace("北京");
        boolean flag = transportationMapper.updateTransportation(transportation);
        assertEquals(true, flag);
    }

    @Test
    public void updateTransportationScoreTest(){
        boolean flag = transportationMapper
                .updateTransportationScore(transportation.getTransportationId(), 10);
        assertEquals(true, flag);
    }

    @Test
    public void updateTransportationFinishFlag(){
        boolean flag = transportationMapper.updateTransportationFinishFlag(transportation.getTransportationId());
        assertEquals(true, flag);
    }

    @Test
    public void findTransportationByTransportationId(){
        Transportation transportation1 = transportationMapper
                .findTransportationByTransportationId(transportation.getTransportationId());
        assertEquals("May", transportation1.getName());
    }
}