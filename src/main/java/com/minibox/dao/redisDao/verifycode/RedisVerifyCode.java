package com.minibox.dao.redisDao.verifycode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author May
 */
@Component
public class RedisVerifyCode implements IRedisVerifyCode {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void addVerifyCode(String phoneNumber, String verifyCode) {
        redisTemplate.opsForValue().set(phoneNumber, verifyCode);
        redisTemplate.expire(phoneNumber, 30, TimeUnit.SECONDS);
    }

    @Override
    public void deleteVerifyCode(String phoneNumber) {
        redisTemplate.delete(phoneNumber);
    }

    @Override
    public String getVerifyCode(String phoneNumber) {
        return redisTemplate.opsForValue().get(phoneNumber);
    }
}
