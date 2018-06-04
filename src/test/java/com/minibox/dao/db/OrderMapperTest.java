package com.minibox.dao.db;

import com.minibox.po.OrderPo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;
    private OrderPo orderPo;

    @Before
    public void before(){
        orderPo = new OrderPo();
        orderPo.setBoxId(101);
        orderPo.setGroupId(1);
        orderPo.setUserId(131);
        orderPo.setDelFlag(0);
    }

    @Test
    public void insertOrderTest(){
        boolean flag = orderMapper.insertOrder(orderPo);
        assertEquals(true, flag);
    }

    @Test
    public void removeOrderTest(){
        boolean flag = orderMapper.removeOrder(88);
        assertEquals(true, flag);
    }

    @Test
    public void findOrderByOrderIdTest(){
        OrderPo orderPo = orderMapper.findOrderByOrderId(88);
        assertEquals(1, orderPo.getGroupId());
    }

    @Test
    public void findOrderByBoxIdTest(){
        OrderPo orderPo = orderMapper.findOrderByBoxId(102);
        assertEquals(89, orderPo.getOrderId());
    }

}