package com.minibox.service;


import com.minibox.constants.ExceptionMessage;
import com.minibox.dao.dbredis.BoxDao;
import com.minibox.dao.dbredis.GroupDao;
import com.minibox.dao.dbredis.OrderDao;
import com.minibox.dao.dbredis.ReservationDao;
import com.minibox.po.BoxPo;
import com.minibox.po.GroupPo;
import com.minibox.po.OrderPo;
import com.minibox.po.ReservationPo;
import com.minibox.service.util.JavaWebToken;
import com.minibox.socket.SocketConnection;
import com.minibox.vo.BoxVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * @author MEI
 */
@Service
public class BoxService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ReservationDao reservationDao;

    @Autowired
    private BoxDao boxDao;

    @Autowired
    private GroupDao groupDao;


    private static final String RESERVATION = "reservation";
    private static final String ORDER = "order";

    public List<BoxVo> getUsingBoxes(String taken){
        int userId = JavaWebToken.getUserIdAndVerifyTakenFromTaken(taken);
        List<BoxPo> boxPos =boxDao.findUsingBoxesByUserId(userId);
        Objects.requireNonNull(boxPos);
        return boxPosConvertBoxVos(boxPos, ORDER);
    }

    public List<BoxVo> getReservingBoxes(String taken) {
        int userId = JavaWebToken.getUserIdAndVerifyTakenFromTaken(taken);
        List<BoxPo> boxPos = boxDao.findReservingBoxedByUserId(userId);
        Objects.requireNonNull(boxPos);
        return boxPosConvertBoxVos(boxPos, RESERVATION);
    }

    public List<BoxVo> getUsingBoxesByPersonId(String personId){
        List<BoxPo> boxPos = boxDao.getUsingBoxesByPersonId(personId);
        return boxPosConvertBoxVos(boxPos, ORDER);
    }

    public void openBoxes(int[] boxIds){
        for (int boxId : boxIds) {
            boxDao.updateBoxStatus(boxId);
            BoxPo boxPo = boxDao.findBoxByBoxId(boxId);
            int groupId = boxPo.getGroupId();
            SocketConnection.sendDataToHardware(groupId, boxId);
        }
    }

    private List<BoxVo> boxPosConvertBoxVos(List<BoxPo> boxPos, String flag){
        List<BoxVo> boxVos = new ArrayList<>();
        boxPos.forEach(boxPo -> {
            if (flag.equals(ORDER)){
                OrderPo orderPo = orderDao.findOrderByBoxId(boxPo.getBoxId());
                Objects.requireNonNull(orderPo, ExceptionMessage.RESOURCE_NOT_FOUND);
                BoxVo boxVo = boxConvert(boxPo, orderPo.getOrderTime() ,orderPo.getOrderId());
                boxVos.add(boxVo);
            }else if (flag.equals(RESERVATION)){
                ReservationPo reservation = reservationDao.findReservationByBoxId(boxPo.getBoxId());
                Objects.requireNonNull(reservation);
                BoxVo box = boxConvert(boxPo, reservation.getOpenTime(), reservation.getReservationId());
                boxVos.add(box);
            }
        });
        Objects.requireNonNull(boxVos);
        return boxVos;
    }

    private BoxVo boxConvert(BoxPo boxPo, String openTime, int id){
        GroupPo group = groupDao.findGroupByGroupId(boxPo.getGroupId());
        return new BoxVo(boxPo.getBoxId(), boxPo.getBoxSize(),
                boxPo.getBoxStatus(), group.getPosition(),
                group.getLat(), group.getLng(), id, openTime);
    }
}
