package com.minibox.dao.db;

import com.minibox.po.UserPo;
import com.minibox.service.util.JavaWebToken;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;
    private UserPo user;

    @Before
    public void before() {
        user = new UserPo();
        user.setEmail("1058752198@qq.com");
        user.setPhoneNumber("15808060133");
        user.setUserName("myj3");
        user.setTrueName("梅勇杰2");
        user.setSex("男");
        user.setPassword("fsdfs");
    }

    @Test
    public void insertUserTest() {
        userMapper.insertUser(user);
        System.out.println(user.getUserId());
    }

    @Test
    public void findUserByPhoneNumberTest() {
        UserPo user = userMapper.findUserByPhoneNumber("15808060138");
        assertEquals(user.getUserName(), "May");
    }

    @Test
    public void findUserByUserNameAndPasswordTest() {
        UserPo user = userMapper.findUserByUserNameAndPassword("May",
                "e10adc3949ba59abbe56e057f20f883e");
        assertEquals(131, user.getUserId());
    }

    @Test
    public void findUserByUserIdTest() {
        UserPo user = userMapper.findUserByUserId(131);
        assertEquals("May", user.getUserName());
    }

    @Test
    public void findUserByPhoneNumberAndPasswordTest(){
        UserPo user = userMapper.findUserByPhoneNumberAndPassword("15808060138", "e10adc3949ba59abbe56e057f20f883e");
        assertEquals(131,user.getUserId());
    }

    @Test
    public void updateUserTest() throws CloneNotSupportedException {
        UserPo userPo = (UserPo) user.clone();
        userPo.setUserName("mkmk");
        userPo.setUserId(135);
        userPo.setPhoneNumber("456456");
        boolean flag = userMapper.updateUser(userPo);
        assertTrue(flag);
    }

    @Test
    public void updateTakenTest() {
        String taken = JavaWebToken.createJavaWebToken(132);
        boolean flag = userMapper.updateTakenByTakenAndUserId(taken, 132);
        Assert.assertTrue(flag);
    }

    @Test
    public void updateUseTimeTest() {
        boolean flag = userMapper.updateUseTime(131);
        Assert.assertTrue(flag);
    }

    @Test
    public void updateAvatar() {
        boolean flag = userMapper.updateAvatarByAvatarAndUserId("1111", 131);
        assertTrue(flag);
    }

    @Test
    public void updatePasswordTest(){
        boolean flag = userMapper.updatePasswordByNewPasswordAndUserId("me.....i",131);
        assertTrue(flag);
    }


}