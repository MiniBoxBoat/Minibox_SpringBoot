package com.minibox.dao.redisDao.reservation;

import com.minibox.po.ReservationPo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

public class RedisReservationDaoTest {

    @Autowired
    private RedisReservationDao redisReservationDao;
    private ReservationPo reservationPo;

    @Before
    public void before(){
        reservationPo = new ReservationPo();
        reservationPo.setReservationId(1);
        reservationPo.setUserName("may");
        reservationPo.setPhoneNumber("158");
        reservationPo.setGroupId(1);
        reservationPo.setBoxSize("小");
        reservationPo.setOpenTime("2018-08-01 12:00:00");
    }

    public void insertReservation() {
        redisReservationDao.insertReservation(reservationPo);
        Optional<ReservationPo> optionalReservation =
                redisReservationDao.findReservationByReservationId(reservationPo.getReservationId());
        redisReservationDao.removeReservationByReservationId(reservationPo.getReservationId());
        assertEquals(1, optionalReservation.get().getGroupId());
    }
}