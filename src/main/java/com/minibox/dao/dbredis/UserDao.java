package com.minibox.dao.dbredis;

import com.minibox.dao.db.UserMapper;
import com.minibox.dao.redisDao.user.RedisUserDao;
import com.minibox.po.UserPo;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDao {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUserDao redisUserDao;

    public boolean insertUser(UserPo user) {
        boolean flag = userMapper.insertUser(user);
        if (!flag) {
            return false;
        } else {
            redisUserDao.insertUser(user);
            return true;
        }
    }

    public UserPo findUserByPhoneNumber(String phoneNumber) {
        return userMapper.findUserByPhoneNumber(phoneNumber);
    }

    public UserPo findUserByUserNameAndPassword(String userName, String password) {
        return userMapper.findUserByPhoneNumberAndPassword(userName, password);
    }

    public UserPo findUserByUserId(int userId) {
        Optional<UserPo> optionalUser = redisUserDao.findUser(userId);
        return optionalUser.orElseGet(() -> {
            UserPo user = userMapper.findUserByUserId(userId);
            if (user != null) {
                redisUserDao.insertUser(user);
            }
            return user;
        });
    }

    public UserPo findUserByUserName(String userName) {
        return userMapper.findUserByUserName(userName);
    }

    public UserPo findUserByPhoneNumberAndPassword(String phoneNumber, String password) {
        return userMapper.findUserByPhoneNumberAndPassword(phoneNumber, password);
    }

    public boolean updateUser(UserPo user) {
        boolean flag = userMapper.updateUser(user);
        if (!flag) {
            return false;
        } else {
            redisUserDao.deleteUser(user.getUserId());
            return true;
        }
    }

    public int increaseUserCredibilityPerWeek() {
        return userMapper.increaseUserCredibilityPerWeek();
    }

    public boolean updateUseTime(int userId) {
        boolean flag = userMapper.updateUseTime(userId);
        if (!flag) {
            return false;
        } else {
            redisUserDao.deleteUser(userId);
            return true;
        }
    }

    public boolean updateAvatarByAvatarAndUserId(String avatar, int userId) {
        boolean flag = userMapper.updateAvatarByAvatarAndUserId(avatar, userId);
        if (!flag) {
            return false;
        } else {
            redisUserDao.deleteUser(userId);
            return true;
        }
    }

    public boolean updatePasswordByNewPasswordAndUserId(String newPassword, int userId) {
        boolean flag = userMapper.updatePasswordByNewPasswordAndUserId(newPassword, userId);
        if (!flag) {
            return false;
        } else {
            redisUserDao.deleteUser(userId);
            return true;
        }
    }

    public boolean updatePersonId(int userId, String personId){
        return userMapper.updatePersonId(userId, personId);
    }
}
