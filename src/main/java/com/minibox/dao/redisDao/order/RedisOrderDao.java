package com.minibox.dao.redisDao.order;

import com.minibox.po.OrderPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.minibox.constants.Redis_Schema.*;

@Component
public class RedisOrderDao {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    public void insertOrder(OrderPo orderPo) {
        redisTemplate.execute(redisConnection -> {
            redisTemplate.opsForValue().set(ORDER_PREFIX + orderPo.getOrderId(), orderPo);
            redisTemplate.opsForSet().add(ORDER_ID_SET_USER + orderPo.getUserId(), orderPo.getOrderId());
            return true;
        },false, true);
    }

    public void removeOrder(int orderId) {
        redisTemplate.delete(ORDER_PREFIX + orderId);
    }

    public Optional<OrderPo> findOrderByOrderId(int orderId) {
        OrderPo order = (OrderPo) redisTemplate.opsForValue().get(ORDER_PREFIX + orderId);
        return Optional.ofNullable(order);
    }
}
