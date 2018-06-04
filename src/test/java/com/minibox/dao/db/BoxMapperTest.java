package com.minibox.dao.db;

import com.minibox.dao.redisDao.verifycode.RedisVerifyCode;
import com.minibox.po.BoxPo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class BoxMapperTest {

    @Autowired
    private BoxMapper boxMapper;

    @Test
    public void updateBoxStatusTest(){
        assertEquals(true, boxMapper.updateBoxStatus(101));
    }

    @Test
    public void findBoxByBoxIdTest(){
        BoxPo boxPo = boxMapper.findBoxByBoxId(101);
        assertEquals(1, boxPo.getGroupId());
    }

    @Test
    public void findUsingBoxesByUserIdTest(){
        List<BoxPo> usingBoxPos = boxMapper.findUsingBoxesByUserId(195);
        assertEquals(2, usingBoxPos.size());
    }

    @Test
    public void findReservingBoxedTest(){
        List<BoxPo> reservingBoxPos = boxMapper.findReservingBoxedByUserId(135);
        assertEquals(1, reservingBoxPos.size());
    }

    @Test
    public void findEmptySmallBoxTest(){
        List<BoxPo> emptySmallBoxPos = boxMapper.findEmptySmallBoxByGroupId(1);
        assertEquals(29, emptySmallBoxPos.size());
    }

    @Test
    public void findEmptySmallBoxCountByGroupIdTest(){
        int emptySmallBoxCount = boxMapper.findEmptySmallBoxCountByGroupId(1);
        assertEquals(30, emptySmallBoxCount);
    }

    @Test
    public void findEmptyLargeBoxByGroupIdTest(){
        List<BoxPo> emptyLargeBoxPos = boxMapper.findEmptyLargeBoxByGroupId(1);
        assertEquals(10, emptyLargeBoxPos.size());
    }

    @Test
    public void findEmptyLargeBoxCountByGroupIdTest(){
        int emptyLargeBoxCount = boxMapper.findEmptyLargeBoxCountByGroupId(1);
        assertEquals(10, emptyLargeBoxCount);
    }
}