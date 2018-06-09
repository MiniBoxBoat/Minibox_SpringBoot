package com.minibox.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaleVo {
    private int saleInfoId;
    private int userId;
    private int boxId;
    private String boxSize;
    private String position;
    private String orderTime;
    private double cost;
}
