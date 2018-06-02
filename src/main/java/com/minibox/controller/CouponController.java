package com.minibox.controller;

import com.minibox.dto.ResponseEntity;
import com.minibox.po.CouponPo;
import com.minibox.service.CouponService;
import com.minibox.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author MEI
 */
@RestController()
@RequestMapping("coupon")
public class CouponController {

    @Autowired
    CouponService couponService;

    @PostMapping("addCoupon.do")
    public ResponseEntity<Object> addCoupon(CouponPo coupon, String taken) {
        couponService.addCoupon(coupon, taken);
        return new ResponseEntity<>(200, Constants.SUCCESS, null);
    }

    @GetMapping("deleteCoupon.do")
    public ResponseEntity<Object> deleteCoupon(int couponId) {
        couponService.deleteCoupon(couponId);
        return new ResponseEntity<>(200, Constants.SUCCESS, null);
    }

    @PostMapping("showCoupon.do")
    public ResponseEntity<List<CouponPo>> showCoupons(String taken) {
        List<CouponPo> coupons = couponService.getCouponsByUserId(taken);
        return new ResponseEntity<>(200, Constants.SUCCESS, coupons);
    }
}
