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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalePo implements Serializable {
    private int saleInfoId;
    private int userId;
    private int boxId;
    private int groupId;
    private String payTime;
    private String orderTime;
    private double cost;
    private int delFlag;
    private int payFlag;
}
