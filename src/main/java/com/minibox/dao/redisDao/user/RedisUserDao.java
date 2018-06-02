package com.minibox.dao.redisDao.user;

import com.minibox.po.UserPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

import static com.minibox.constants.Redis_Schema.USER_ID_SET;
import static com.minibox.constants.Redis_Schema.USER_PREFIX;

/**
 * @author May
 */

@Slf4j
@Component
public class RedisUserDao {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    public void insertUser(UserPo user) {
        redisTemplate.execute(redisConnection -> {
            redisTemplate.opsForValue().set(USER_PREFIX + user.getUserId(), user);
            redisTemplate.opsForSet().add(USER_ID_SET, user.getUserId());
            return true;
        }, false, true);

    }

    public void deleteUser(int userId) {
         redisTemplate.delete(USER_PREFIX + userId);
    }

    public void deleteAllRedisUser() {
        redisTemplate.execute(redisConnection -> {
            Set<Object> userIds = redisTemplate.opsForSet().members(USER_ID_SET);
            userIds.forEach(userId ->
                    redisTemplate.delete(USER_PREFIX + (int) userId));
            return true;
        }, false, true);
    }

    public Optional<UserPo> findUser(int userId) {
        UserPo user = (UserPo) redisTemplate.opsForValue().get(USER_PREFIX + userId);
        return Optional.ofNullable(user);
    }
}
