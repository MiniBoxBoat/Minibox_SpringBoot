package com.minibox.service;

import com.minibox.constants.ExceptionMessage;
import com.minibox.dao.db.BoxMapper;
import com.minibox.dao.db.GroupMapper;
import com.minibox.dao.db.OrderMapper;
import com.minibox.dao.db.UserMapper;
import com.minibox.dao.dbredis.BoxDao;
import com.minibox.dao.dbredis.GroupDao;
import com.minibox.dao.dbredis.OrderDao;
import com.minibox.dao.dbredis.UserDao;
import com.minibox.dao.redisDao.box.RedisBoxDao;
import com.minibox.dao.redisDao.group.RedisGroupDao;
import com.minibox.dao.redisDao.order.RedisOrderDao;
import com.minibox.dao.redisDao.user.RedisUserDao;
import com.minibox.dto.OrderDto;
import com.minibox.exception.ParameterException;
import com.minibox.exception.RollbackException;
import com.minibox.po.BoxPo;
import com.minibox.po.OrderPo;
import com.minibox.po.UserPo;
import com.minibox.service.util.HardwareCheck;
import com.minibox.service.util.JavaWebToken;
import com.minibox.service.util.ServiceExceptionChecking;
import com.minibox.socket.SocketConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.SimpleFormatter;

import static com.minibox.constants.BoxSize.SMALL;
import static com.minibox.service.util.ServiceExceptionChecking.checkBoxSizeIsTrue;

@Service
public class OrderService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private BoxDao boxDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private OrderDao orderDao;

    @Transactional(rollbackFor = RollbackException.class)
    public void addOrder(OrderDto orderDto, String taken) {
        int userId = JavaWebToken.getUserIdAndVerifyTakenFromTaken(taken);
        checkAddOrderParameters(orderDto);
        List<Integer> canUseBoxesId = getCanUseBoxesId(orderDto);
        ServiceExceptionChecking.checkBoxSurplus(canUseBoxesId.size());
        UserPo user = userDao.findUserByUserId(userId);
        List<OrderPo> orderPos = orderDtoToOrderPoList(orderDto, userId, canUseBoxesId);

        HardwareCheck.checkHardwareIsOnline(orderDto.getGroupId());
        if (!orderPos.stream().allMatch(orderDao::insertOrder)|| !userDao.updateUseTime(user.getUserId())
                || !groupDao.reduceGroupBoxNum(orderDto.getGroupId(), orderDto.getBoxNum())
                || !canUseBoxesId.stream().allMatch(boxId -> boxDao.updateBoxStatus(boxId))) {
            throw new RollbackException();
        }

        canUseBoxesId.stream().forEach(boxId -> {
            SocketConnection.sendDataToHardware(orderDto.getGroupId(), boxId);
        });
    }

    private List<Integer> getCanUseBoxesId(OrderDto orderDto) {
        List<Integer> boxIdList = new ArrayList<>();
        if (orderDto.getBoxSize().equals(SMALL.size())) {
            List<BoxPo> smallBoxPos = boxDao.findEmptySmallBoxByGroupId(orderDto.getGroupId());
            if (smallBoxPos.size() == 0) {
                throw new ParameterException(ExceptionMessage.NO_BOX);
            }
            for (int i = 0; i < orderDto.getBoxNum(); i++) {
                boxIdList.add(smallBoxPos.get(i).getBoxId());
            }
        } else {
            List<BoxPo> largeBoxPos = boxDao.findEmptyLargeBoxByGroupId(orderDto.getGroupId());
            if (largeBoxPos.size() == 0) {
                throw new ParameterException(ExceptionMessage.NO_BOX);
            }
            for (int i = 0; i < orderDto.getBoxNum(); i++) {
                boxIdList.add(largeBoxPos.get(i).getBoxId());
            }
        }
        return boxIdList;
    }

    private void checkAddOrderParameters(OrderDto orderDto) {
        if (orderDto.getGroupId() == null || orderDto.getBoxSize() == null
                || orderDto.getBoxNum() == null) {
            throw new ParameterException(ExceptionMessage.PARAMETER_IS_NOT_FULL);
        }
        checkBoxSizeIsTrue(orderDto.getBoxSize());
    }

    private List<OrderPo> orderDtoToOrderPoList(OrderDto orderDto, int userId, List<Integer> canUseBoxIds) {
        List<OrderPo> orderPos = new LinkedList<>();
        canUseBoxIds.forEach(canUseBoxId -> {
            OrderPo order = new OrderPo();
            order.setGroupId(orderDto.getGroupId());
            order.setUserId(userId);
            order.setBoxId(canUseBoxId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String nowDate = formatter.format(LocalDateTime.now());
            order.setOrderTime(nowDate);
            orderPos.add(order);
        });
        return orderPos;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(int orderId) {
        OrderPo orderPo = orderDao.findOrderByOrderId(orderId);
        int boxId = orderPo.getBoxId();
        int groupId = orderPo.getGroupId();
        if (!(orderDao.removeOrder(orderId) || !boxDao.updateBoxStatus(boxId)
                || !groupDao.increaseGroupBoxNum(groupId, 1))) {
            throw new RollbackException();
        }
    }

}
