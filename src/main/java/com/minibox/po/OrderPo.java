package com.minibox.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author MEI
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderPo implements Serializable {
    private int orderId;
    private int userId;
    private int groupId;
    private int boxId;
    private String orderTime;
    private int delFlag;
}
