package com.minibox.service;

import com.minibox.dao.db.CouponMapper;
import com.minibox.dao.dbredis.CouponDao;
import com.minibox.dao.redisDao.coupon.RedisCouponDao;
import com.minibox.po.CouponPo;
import com.minibox.service.util.JavaWebToken;
import com.minibox.service.util.ServiceExceptionChecking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author MEI
 */
@Service
public class CouponService {

    @Autowired
    private CouponDao couponDao;

    public void addCoupon(CouponPo coupon, String taken) {
        checkAddCouponParameters(coupon);
        int userId = JavaWebToken.getUserIdAndVerifyTakenFromTaken(taken);
        coupon.setUserId(userId);
        ServiceExceptionChecking.checkSqlExcute(couponDao.insertCoupon(coupon));
    }

    private void checkAddCouponParameters(CouponPo coupon){
        ServiceExceptionChecking.checkTimeIsInPattern(coupon.getDeadlineTime());
        ServiceExceptionChecking.checkTimeIsAfterNow(coupon.getDeadlineTime());
    }

    public void deleteCoupon(int couponId) {
        ServiceExceptionChecking.checkSqlExcute(couponDao.removeCoupon(couponId));
    }

    public List<CouponPo> getCouponsByUserId(String taken) {
        int userId = JavaWebToken.getUserIdAndVerifyTakenFromTaken(taken);
        return couponDao.findCouponsByUserId(userId);
    }
}
