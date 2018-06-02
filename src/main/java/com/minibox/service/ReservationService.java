package com.minibox.service;

import com.minibox.constants.ExceptionMessage;
import com.minibox.dao.db.*;
import com.minibox.dao.dbredis.*;
import com.minibox.dao.redisDao.box.RedisBoxDao;
import com.minibox.dao.redisDao.group.RedisGroupDao;
import com.minibox.dao.redisDao.order.RedisOrderDao;
import com.minibox.dao.redisDao.reservation.RedisReservationDao;
import com.minibox.dao.redisDao.user.RedisUserDao;
import com.minibox.dto.ReservationDto;
import com.minibox.exception.HardwareException;
import com.minibox.exception.ParameterException;
import com.minibox.exception.RollbackException;
import com.minibox.exception.ServerException;
import com.minibox.po.BoxPo;
import com.minibox.po.GroupPo;
import com.minibox.po.OrderPo;
import com.minibox.po.ReservationPo;
import com.minibox.service.util.HardwareCheck;
import com.minibox.service.util.JavaWebToken;
import com.minibox.service.util.ServiceExceptionChecking;
import com.minibox.socket.SocketConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.minibox.constants.BoxSize.SMALL;

/**
 * `
 *
 * @author MEI
 */
@Service
public class ReservationService {

    @Autowired
    private ReservationDao reservationDao;

    @Autowired
    private BoxDao boxDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private OrderDao orderDao;

    @Transactional(rollbackFor = Exception.class)
    public void addReservationsAndUpdateBoxesStatusAndReduceGroupBoxNum(ReservationDto reservationDto,
                                                                        String taken) {
        int userId = JavaWebToken.getUserIdAndVerifyTakenFromTaken(taken);
        checkAddReservationParameter(reservationDto);
        List<Integer> canUseBoxesId = getCanUseBoxesId(reservationDto);
        ServiceExceptionChecking.checkBoxSurplus(canUseBoxesId.size());

        HardwareCheck.checkHardwareIsOnline(reservationDto.getGroupId());
        boolean successInsert = canUseBoxesId.stream().allMatch(boxId -> {
            ReservationPo reservation = reservationDtoToReservationPo(reservationDto, userId);
            reservation.setBoxId(boxId);
            return reservationDao.insertReservation(reservation);
        });
        boolean successReduceGroupBoxNum = groupDao.reduceGroupBoxNum(reservationDto.getGroupId(),
                reservationDto.getBoxNum());

        boolean successUpdateAllUseBoxesStatus = canUseBoxesId.stream()
                .allMatch(boxId -> boxDao.updateBoxStatus(boxId));

        ServiceExceptionChecking.checkSqlExcute(successInsert && successReduceGroupBoxNum && successUpdateAllUseBoxesStatus);
    }

    private List<Integer> getCanUseBoxesId(ReservationDto reservationDto) {
        List<Integer> boxIdList = new ArrayList<>();
        if (reservationDto.getBoxSize().equals(SMALL.size())) {
            List<BoxPo> smallBoxPos = boxDao.findEmptySmallBoxByGroupId(reservationDto.getGroupId());
            if (smallBoxPos.size() == 0) {
                throw new ParameterException(ExceptionMessage.NO_BOX);
            }
            for (int i = 0; i < reservationDto.getBoxNum(); i++) {
                boxIdList.add(smallBoxPos.get(i).getBoxId());
            }
        } else {
            List<BoxPo> largeBoxPos = boxDao.findEmptyLargeBoxByGroupId(reservationDto.getGroupId());
            if (largeBoxPos.size() == 0) {
                throw new ParameterException(ExceptionMessage.NO_BOX);
            }
            for (int i = 0; i < reservationDto.getBoxNum(); i++) {
                boxIdList.add(largeBoxPos.get(i).getBoxId());
            }
        }
        return boxIdList;
    }

    private ReservationPo reservationDtoToReservationPo(ReservationDto reservationDto, int userId) {
        return ReservationPo.builder()
                .boxSize(reservationDto.getBoxSize())
                .groupId(reservationDto.getGroupId())
                .openTime(reservationDto.getOpenTime())
                .phoneNumber(reservationDto.getPhoneNumber())
                .userId(userId)
                .userName(reservationDto.getUserName())
                .useTime(reservationDto.getUseTime())
                .build();
    }

    private void checkAddReservationParameter(ReservationDto reservationDto) {
        if (reservationDto.getUserName() == null || reservationDto.getOpenTime() == null ||
                reservationDto.getUseTime() == null || reservationDto.getBoxSize() == null
                || reservationDto.getPhoneNumber() == null || reservationDto.getGroupId() == 0) {
            throw new ParameterException(ExceptionMessage.PARAMETER_IS_NOT_FULL);
        }
        ServiceExceptionChecking.checkPhoneNumberIsTrue(reservationDto.getPhoneNumber());
        ServiceExceptionChecking.checkTimeIsInPattern(reservationDto.getOpenTime());
        ServiceExceptionChecking.checkTimeIsAfterNow(reservationDto.getOpenTime());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteReservationByReservationId(int reservationId) {
        boolean flag = reservationDao.removeReservationByReservationId(reservationId);
        if (!flag){
            throw new ServerException();
        }
    }

    @Transactional(rollbackFor = ServerException.class)
    public void deleteReservationAndAddOrderAndUpdateBoxStatusAndUpdateUseTime(int reservationId, String taken)
    {
        int userId = JavaWebToken.getUserIdAndVerifyTakenFromTaken(taken);
        ReservationPo reservation = reservationDao.findReservationByReservationId(reservationId);
        Objects.requireNonNull(reservation, ExceptionMessage.RESOURCE_NOT_FOUND);
        GroupPo group = groupDao.findGroupByGroupId(reservation.getGroupId());

        if (SocketConnection.map.get(group.getGroupId()) == null){
            boxDao.updateBoxStatus(reservation.getBoxId());
            throw new HardwareException(ExceptionMessage.HARDWARE_NOT_ONLINE);
        }

        OrderPo orderPo = getOrderFromGroupAndReservation(userId, reservation, group);
        ServiceExceptionChecking.checkSqlExcute((reservationDao.removeReservationByReservationId(reservationId)
                && orderDao.insertOrder(orderPo)
                && boxDao.updateBoxStatus(reservation.getBoxId())
                && userDao.updateUseTime(userId)));

        SocketConnection.sendDataToHardware(group.getGroupId(), reservation.getBoxId());
    }

    private OrderPo getOrderFromGroupAndReservation(int userId, ReservationPo reservation, GroupPo group) {
        OrderPo orderPo = new OrderPo();
        orderPo.setGroupId(group.getGroupId());
        orderPo.setBoxId(reservation.getBoxId());
        orderPo.setUserId(userId);
        orderPo.setOrderTime(LocalDateTime.now().toString());
        return orderPo;
    }

    public void updateReservation(ReservationPo reservation) {
        checkUpdateReservationParameter(reservation);
        ServiceExceptionChecking.checkSqlExcute(reservationDao.updateReservation(reservation));
    }

    private void checkUpdateReservationParameter(ReservationPo reservation) {
        if (reservation.getUserName() == null || reservation.getOpenTime() == null || reservation.getUseTime() == 0
                || reservation.getPhoneNumber() == null) {
            throw new ParameterException(ExceptionMessage.PARAMETER_IS_NOT_FULL);
        }
        ServiceExceptionChecking.checkTimeIsAfterNow(reservation.getOpenTime());
        ServiceExceptionChecking.checkTimeIsInPattern(reservation.getPhoneNumber());
    }

    public List<ReservationPo> getReservation(String taken) {
        int userId = JavaWebToken.getUserIdAndVerifyTakenFromTaken(taken);
        return reservationDao.findReservationsByUserId(userId);
    }
}
