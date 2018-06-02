package com.minibox.schedule_task;

import com.minibox.dao.db.ReservationMapper;
import com.minibox.dao.dbredis.UserDao;
import com.minibox.dao.redisDao.user.RedisUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author MEI
 */
@Component
public class ScheduleTaskImp implements ScheduleTask{
    private Logger logger = LoggerFactory.getLogger(ScheduleTaskImp.class);

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisUserDao redisUserDao;

    @Scheduled(cron = "0 0 0 ? * MON")
    @Override
    public void addCredibility() {
        logger.info("进行了一次一周一次的更新信誉值");
        int changeNum = userDao.increaseUserCredibilityPerWeek();
        if (changeNum > 0){
            redisUserDao.deleteAllRedisUser();
        }
    }

    @Scheduled(fixedDelay = 10000)
    @Override
    public void checkReservation() {
        reservationMapper.updateOverdueReservationExpFlag();
    }
}
