package com.minibox.dao.redisDao.verifycode;

public interface IRedisVerifyCode {
    void addVerifyCode(String phoneNumber, String verifyCode);

    void deleteVerifyCode(String phoneNumber);

    String getVerifyCode(String phoneNumber);
}
