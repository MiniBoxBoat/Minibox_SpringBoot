package com.minibox.dao.dbredis;

import com.minibox.dao.db.BoxMapper;
import com.minibox.dao.redisDao.box.RedisBoxDao;
import com.minibox.po.BoxPo;
import com.minibox.po.OrderPo;
import com.minibox.po.ReservationPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.minibox.constants.Redis_Schema.ORDER_ID_SET_USER;
import static com.minibox.constants.Redis_Schema.RESERVATION_ID_SET_USER;

@Component
public class BoxDao {

    @Autowired
    private RedisBoxDao redisBoxDao;

    @Autowired
    private BoxMapper boxMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ReservationDao reservationDao;

    public boolean updateBoxStatus(int boxId) {
        boolean flag = boxMapper.updateBoxStatus(boxId);
        if (!flag) {
            return false;
        } else {
            redisBoxDao.removeBoxByBoxId(boxId);
            return true;
        }
    }

    public BoxPo findBoxByBoxId(int boxId) {
        Optional<BoxPo> optionalBox = redisBoxDao.findBoxByBoxId(boxId);
        return optionalBox.orElseGet(() -> {
            BoxPo boxPo = boxMapper.findBoxByBoxId(boxId);
            if (boxPo != null) {
                redisBoxDao.insertBox(boxPo);
            }
            return boxPo;
        });
    }

    public List<BoxPo> findUsingBoxesByUserId(int userId) {
        Set<String> orderIds = redisTemplate.opsForSet().members(ORDER_ID_SET_USER + userId);
        return redisTemplate.execute(redisConnection -> {
            List<OrderPo> orderPos = new LinkedList<>();
            List<Integer> boxIds = new LinkedList<>();
            List<BoxPo> boxPos = new LinkedList<>();
            orderIds.forEach(orderId -> {
                OrderPo orderPo = orderDao.findOrderByOrderId(Integer.valueOf(orderId));
                orderPos.add(orderPo);
            });
            orderPos.forEach(order -> {
                boxIds.add(order.getBoxId());
            });
            boxIds.forEach(boxId -> {
                BoxPo box = findBoxByBoxId(Integer.valueOf(boxId));
                boxPos.add(box);
            });
            return boxPos;
        }, false, true);
    }

    public List<BoxPo> findReservingBoxedByUserId(int userId) {
        Set<String> reservationIds = redisTemplate.opsForSet().members(RESERVATION_ID_SET_USER + userId);
        return redisTemplate.execute(redisConnection -> {
            List<ReservationPo> reservationPos = new LinkedList<>();
            List<Integer> boxIds = new LinkedList<>();
            List<BoxPo> boxPos = new LinkedList<>();
            reservationIds.forEach(reservationId -> {
                ReservationPo reservation = reservationDao.
                        findReservationByReservationId(Integer.valueOf(reservationId));
                reservationPos.add(reservation);
            });
            reservationPos.forEach(reservation -> {
                boxIds.add(reservation.getBoxId());
            });
            boxIds.forEach(boxId -> {
                BoxPo box = findBoxByBoxId(boxId);
                boxPos.add(box);
            });
            return boxPos;
        }, false, true);
    }

    public List<BoxPo> findEmptySmallBoxByGroupId(int groupId) {
        return boxMapper.findEmptySmallBoxByGroupId(groupId);
    }

    public int findEmptySmallBoxCountByGroupId(int groupId) {
        return boxMapper.findEmptySmallBoxCountByGroupId(groupId);
    }

    public List<BoxPo> findEmptyLargeBoxByGroupId(int groupId) {
        return boxMapper.findEmptyLargeBoxByGroupId(groupId);
    }

    public int findEmptyLargeBoxCountByGroupId(int groupId) {
        return boxMapper.findEmptyLargeBoxCountByGroupId(groupId);
    }


}
