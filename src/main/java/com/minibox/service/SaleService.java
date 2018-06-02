package com.minibox.service;

import com.minibox.dao.db.OrderMapper;
import com.minibox.dao.db.SaleMapper;
import com.minibox.po.OrderPo;
import com.minibox.po.SalePo;
import com.minibox.socket.SocketConnection;
import com.minibox.service.util.ServiceExceptionChecking;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class SaleService {

    @Resource
    private SaleMapper saleMapper;

    @Resource
    private OrderMapper orderMapper;

    @Transactional(rollbackFor = Exception.class)
    public void addSaleInfoAndRemoveOrder(int orderId, double cost){
        SalePo sale = getSaleByOrderIdAndCost(orderId, cost);
        ServiceExceptionChecking.checkSqlExcute(saleMapper.insertSaleInfo(sale) && orderMapper.removeOrder(orderId));

        OrderPo order = orderMapper.findOrderByOrderId(orderId);
        SocketConnection.sendDataToHardware(order.getGroupId(), orderId);
    }

    private SalePo getSaleByOrderIdAndCost(int orderId, double cost){
        OrderPo orderPo = orderMapper.findOrderByOrderId(orderId);
        return SalePo.builder()
                .boxId(orderPo.getBoxId())
                .cost(cost)
                .groupId(orderPo.getGroupId())
                .orderTime(orderPo.getOrderTime())
                .userId(orderPo.getUserId())
                .build();
    }
}
