package com.minibox.dao.redisDao.user;

import com.minibox.po.UserPo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import static org.junit.Assert.assertEquals;

public class RedisUserDaoTest {

    @Autowired
    private RedisUserDao redisUserDao;
    private UserPo userPo;

    @Before
    public void before(){
        userPo = new UserPo();
        userPo.setPassword("fd");
        userPo.setEmail("fdsf");
        userPo.setUserName("bb");
        userPo.setUserId(1);
    }

    public void insertUser() {
        redisUserDao.insertUser(userPo);
        Optional<UserPo> optionalUser = redisUserDao.findUser(userPo.getUserId());
        redisUserDao.deleteUser(userPo.getUserId());
        assertEquals("fd", optionalUser.get().getPassword());
    }
}