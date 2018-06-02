package com.minibox.dao.redisDao.order;

import com.minibox.po.OrderPo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import static org.junit.Assert.assertEquals;

public class RedisOrderDaoTest {

    @Autowired
    private RedisOrderDao redisOrderDao;
    private OrderPo orderPo;

    @Before
    public void before(){
        orderPo = new OrderPo();
        orderPo.setOrderId(1);
        orderPo.setOrderTime("2018-01-02 12:00:00");
        orderPo.setGroupId(1);
        orderPo.setBoxId(1);
        orderPo.setUserId(2);
        orderPo.setDelFlag(0);
    }

    public void findOrderByOrderId() {
        redisOrderDao.insertOrder(orderPo);
        Optional<OrderPo> optionalOrder = redisOrderDao.findOrderByOrderId(orderPo.getOrderId());
        redisOrderDao.removeOrder(orderPo.getOrderId());
        assertEquals(2, optionalOrder.get().getUserId());
    }
}