package com.minibox.dao.dbredis;

import com.minibox.dao.db.OrderMapper;
import com.minibox.dao.redisDao.order.RedisOrderDao;
import com.minibox.po.OrderPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderDao {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisOrderDao redisOrderDao;

    public boolean insertOrder(OrderPo orderPo) {
        boolean flag = orderMapper.insertOrder(orderPo);
        if (!flag){
            return false;
        }else {
            redisOrderDao.insertOrder(orderPo);
            return true;
        }
    }

    public boolean removeOrder(int orderId) {
        boolean flag = orderMapper.removeOrder(orderId);
        if (!flag){
            return false;
        }else {
            redisOrderDao.removeOrder(orderId);
            return true;
        }
    }

    public OrderPo findOrderByOrderId(int orderId) {
        Optional<OrderPo> optionalOrder = redisOrderDao.findOrderByOrderId(orderId);
        return optionalOrder.orElseGet(()-> {
            OrderPo order = orderMapper.findOrderByOrderId(orderId);
            if (order != null){
                redisOrderDao.insertOrder(order);
            }
            return order;
        });
    }

    public OrderPo findOrderByBoxId(int boxId) {
        return orderMapper.findOrderByBoxId(boxId);
    }
}
