package com.minibox.po;

import lombok.Data;

import java.io.Serializable;

/**
 * @author MEI
 */
@Data
public class CouponPo implements Serializable {
    private int couponId;
    private int userId;
    private double money;
    private String deadlineTime;
    private int delFlag;
}
