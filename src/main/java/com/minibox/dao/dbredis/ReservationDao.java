package com.minibox.dao.dbredis;

import com.minibox.dao.db.ReservationMapper;
import com.minibox.dao.redisDao.reservation.RedisReservationDao;
import com.minibox.po.ReservationPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.minibox.constants.Redis_Schema.RESERVATION_ID_SET_USER;

@Component
public class ReservationDao {

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private RedisReservationDao redisReservationDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean insertReservation(ReservationPo reservation) {
        boolean flag = reservationMapper.insertReservation(reservation);
        if (!flag) {
            return false;
        } else {
            redisReservationDao.insertReservation(reservation);
            return true;
        }
    }

    public boolean removeReservationByReservationId(int reservationId) {
        boolean flag = reservationMapper.removeReservationByReservationId(reservationId);
        if (!flag) {
            return false;
        } else {
            ReservationPo reservationPo = findReservationByReservationId(reservationId);
            redisReservationDao.removeReservationByReservationId(reservationId);
            redisTemplate.opsForSet().remove(RESERVATION_ID_SET_USER + reservationPo.getUserId(),
                    String.valueOf(reservationId));
            return true;
        }
    }

    public boolean updateReservation(ReservationPo reservation) {
        boolean flag = reservationMapper.updateReservation(reservation);
        if (!flag) {
            return false;
        } else {
            redisReservationDao.removeReservationByReservationId(reservation.getReservationId());
            return true;
        }
    }

    public List<ReservationPo> findReservationsByUserId(int userId) {
        Set<String> reservationIds = redisTemplate.opsForSet().members(RESERVATION_ID_SET_USER + userId);
        return redisTemplate.execute(redisConnection -> {
            List<ReservationPo> reservationPos = new LinkedList<>();
            reservationIds.forEach(reservationId -> {
                Optional<ReservationPo> optionalReservation =
                        redisReservationDao.findReservationByReservationId(Integer.valueOf(reservationId));
                ReservationPo reservation = optionalReservation.orElseGet(() ->
                        reservationMapper.findReservationByReservationId(Integer.valueOf(reservationId)));
                reservationPos.add(reservation);
            });
            return reservationPos;
        }, false, true);
    }

    public ReservationPo findReservationByReservationId(int reservationId) {
        Optional<ReservationPo> optionalReservation =
                redisReservationDao.findReservationByReservationId(reservationId);
        return optionalReservation.orElseGet(() -> {
            ReservationPo reservation = reservationMapper.findReservationByReservationId(reservationId);
            if (reservation != null){
                redisReservationDao.insertReservation(reservation);
            }
            return reservation;
        });
    }

    public boolean updateOverdueReservationExpFlag() {
        return reservationMapper.updateOverdueReservationExpFlag();
    }

    public ReservationPo findReservationByBoxId(int boxId) {
        return reservationMapper.findReservationByBoxId(boxId);
    }
}
