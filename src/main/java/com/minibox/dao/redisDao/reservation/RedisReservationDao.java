package com.minibox.dao.redisDao.reservation;

import com.minibox.exception.ServerException;
import com.minibox.po.ReservationPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static com.minibox.constants.Redis_Schema.RESERVATION_ID_SET_USER;
import static com.minibox.constants.Redis_Schema.RESERVATION_PREFIX;

@Component
public class RedisReservationDao {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    public void insertReservation(ReservationPo reservation) {
        redisTemplate.execute(redisConnection -> {
            try {
                redisTemplate.opsForValue().set(RESERVATION_PREFIX + reservation.getReservationId(), reservation);
                redisTemplate.opsForSet().add(RESERVATION_ID_SET_USER + reservation.getUserId(),
                        reservation.getReservationId());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date expireDate = dateFormat.parse(reservation.getOpenTime());
                redisTemplate.expireAt(RESERVATION_PREFIX + reservation.getReservationId(), expireDate);
            } catch (ParseException e) {
                throw new ServerException();
            }
            return true;
        }, false, true);
    }

    public void removeReservationByReservationId(int reservationId) {
        redisTemplate.delete(RESERVATION_PREFIX + reservationId);
    }

    public Optional<ReservationPo> findReservationByReservationId(int reservationId) {
        ReservationPo reservationPo = (ReservationPo) redisTemplate.opsForValue().get(RESERVATION_PREFIX + reservationId);
        return Optional.ofNullable(reservationPo);
    }


}
