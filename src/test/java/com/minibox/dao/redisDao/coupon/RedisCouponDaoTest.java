package com.minibox.dao.redisDao.coupon;

import com.minibox.po.CouponPo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class RedisCouponDaoTest {

    @Autowired
    private RedisCouponDao redisCouponDao;
    private CouponPo couponPo;

    @Before
    public void before(){
        couponPo = new CouponPo();
        couponPo.setCouponId(1);
        couponPo.setDelFlag(0);
        couponPo.setDeadlineTime("2018-01-01 12:00:00");
        couponPo.setMoney(5);
        couponPo.setUserId(1);
    }

    public void findCouponByCouponId() {
        redisCouponDao.insertCoupon(couponPo);
        Optional<CouponPo> coupon = redisCouponDao.findCouponByCouponId(couponPo.getCouponId());
        redisCouponDao.removeCoupon(1);
        assertEquals(1, coupon.get().getUserId());
    }

}