package com.minibox.dao.db;

import com.minibox.po.CouponPo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CouponMapperTest {

    @Autowired
    private CouponMapper couponMapper;
    private CouponPo coupon;

    @Before
    public void before(){
        coupon = new CouponPo();
        coupon.setUserId(131);
        coupon.setMoney(10);
        coupon.setDeadlineTime("2018-2-3 12:00:00");
    }

    @Test
    public void insertCouponTest(){
        Assert.assertEquals(true, couponMapper.insertCoupon(coupon));
    }

    @Test
    public void removeCouponTest(){
        Assert.assertEquals(true,  couponMapper.removeCoupon(2));
    }

    @Test
    public void findCouponsByUserIdTest(){
        List<CouponPo> coupons = couponMapper.findCouponsByUserId(2);
        assertEquals(4, coupons.size());
    }

}