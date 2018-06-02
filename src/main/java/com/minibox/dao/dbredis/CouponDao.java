package com.minibox.dao.dbredis;

import com.minibox.dao.db.CouponMapper;
import com.minibox.dao.redisDao.coupon.RedisCouponDao;
import com.minibox.po.CouponPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.minibox.constants.Redis_Schema.COUPON_ID_SET_USER;

@Component
public class CouponDao {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private RedisCouponDao redisCouponDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean insertCoupon(CouponPo coupon) {
        if (couponMapper.insertCoupon(coupon)){
            redisCouponDao.insertCoupon(coupon);
            return true;
        }
        return false;
    }

    public boolean removeCoupon(int couponId) {
        boolean flag = couponMapper.removeCoupon(couponId);
        if (!flag) {
            return false;
        } else {
            CouponPo couponPo = findCouponByCouponId(couponId);
            redisCouponDao.removeCoupon(couponId);
            redisTemplate.opsForSet().remove(COUPON_ID_SET_USER + couponPo.getUserId(), couponId);
            return true;
        }
    }

    public List<CouponPo> findCouponsByUserId(int userId) {
        List<CouponPo> couponPos = new LinkedList<>();
        Set<String> couponIdList =
                redisTemplate.opsForSet().members(COUPON_ID_SET_USER + userId);
        return redisTemplate.execute(redisConnection -> {
            couponIdList.forEach(couponId -> {
                System.out.println(couponId);
                couponPos.add(findCouponByCouponId(Integer.valueOf(couponId)));
            });
            System.out.println(couponPos.get(0).getDeadlineTime());
            return couponPos;
        }, false, true);
    }

    public CouponPo findCouponByCouponId(int couponId) {
        Optional<CouponPo> optionalCoupon = redisCouponDao.findCouponByCouponId(couponId);
        return optionalCoupon.orElseGet(() -> {
            CouponPo coupon = couponMapper.findCouponByCouponId(couponId);
            if (coupon != null) {
                redisCouponDao.insertCoupon(coupon);
            }
            return coupon;
        });
    }
}
