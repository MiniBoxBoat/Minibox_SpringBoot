package com.minibox.dao.redisDao.coupon;

import com.minibox.po.CouponPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.minibox.constants.Redis_Schema.COUPON_ID_SET_USER;
import static com.minibox.constants.Redis_Schema.COUPON_PREFIX;

@Component
public class RedisCouponDao {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    public void insertCoupon(CouponPo coupon) {
        redisTemplate.execute(redisConnection -> {
            redisTemplate.opsForValue().set(COUPON_PREFIX + coupon.getCouponId(), coupon);
            redisTemplate.opsForSet().add(COUPON_ID_SET_USER + coupon.getUserId(), coupon.getCouponId());
            return true;
        },false, true);

    }

    public void removeCoupon(int couponId) {
        redisTemplate.delete(COUPON_PREFIX + couponId);
    }


    public Optional<CouponPo> findCouponByCouponId(int couponId){
        CouponPo coupon = (CouponPo) redisTemplate.opsForValue().get(COUPON_PREFIX + couponId);
        return Optional.ofNullable(coupon);
    }

    public void addCouponIdToCouponIdSetUser(int couponId, int userId){
        redisTemplate.opsForSet().add(COUPON_ID_SET_USER + userId, String.valueOf(couponId));
    }
}
