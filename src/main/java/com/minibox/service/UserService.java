package com.minibox.service;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.minibox.constants.ExceptionMessage;
import com.minibox.dao.db.UserMapper;
import com.minibox.dao.dbredis.UserDao;
import com.minibox.dao.redisDao.user.RedisUserDao;
import com.minibox.dao.redisDao.verifycode.RedisVerifyCode;
import com.minibox.exception.ParameterException;
import com.minibox.exception.SendSmsFailedException;
import com.minibox.exception.ServerException;
import com.minibox.exception.VerifyCodeException;
import com.minibox.po.UserPo;
import com.minibox.service.util.JavaWebToken;
import com.minibox.service.util.RamdomNumberUtil;
import com.minibox.service.util.ServiceExceptionChecking;
import com.minibox.service.util.Sms;
import com.minibox.vo.UserVo;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

import static com.minibox.constants.ExceptionMessage.RESOURCE_NOT_FOUND;

/**
 * @author MEI
 */
@Service
@Slf4j
public class UserService {
    private static final String SMS_SUCCESS_CODE = "OK";

    @Resource
    private UserDao userDao;

    @Resource
    private RedisVerifyCode redisVerifyCode;

    public UserVo addUserAndCheckVerifyCode(UserPo user, String verifyCode) {
        checkUserParameterAndIfDouble(user);
        checkVerifyCode(user.getPhoneNumber(), verifyCode);
        if (!userDao.insertUser(user)) {
            throw new ServerException();
        }
        return userPoToUserVo(user);
    }

    private void checkUserParameterAndIfDouble(UserPo user) {
        if (user.getUserName() == null || user.getSex() == null
                || user.getPhoneNumber() == null) {
            throw new ParameterException(ExceptionMessage.PARAMETER_IS_NOT_FULL);
        }
        ServiceExceptionChecking.checkUserNameIsTooLong(user.getUserName());
        ServiceExceptionChecking.checkPhoneNumberIsTrue(user.getPhoneNumber());
        ServiceExceptionChecking.checkPhoneNumberIsUsed(userDao.findUserByPhoneNumber(user.getPhoneNumber()));

        UserPo userGetByPhoneNumber = userDao.findUserByPhoneNumber(user.getPhoneNumber());
        ServiceExceptionChecking.checkPhoneNumberIsUsed(userGetByPhoneNumber);
        UserPo userGetByUserName = userDao.findUserByUserName(user.getUserName());
        ServiceExceptionChecking.checkUserNameIsUsed(userGetByUserName);
    }

    public UserVo checkUser(String phoneNumber, String password) {
        ServiceExceptionChecking.checkPhoneNumberIsTrue(phoneNumber);
        UserPo user = userDao.findUserByPhoneNumber(phoneNumber);
        Objects.requireNonNull(user, ExceptionMessage.USER_NOT_EXSTS);
        if (!user.getPassword().equals(password)) {
            throw new ParameterException(ExceptionMessage.PASSWORD_IS_WRONG);
        }
        UserPo userPo = userDao.findUserByPhoneNumberAndPassword(phoneNumber, password);
        String taken = JavaWebToken.createJavaWebToken(user.getUserId());
        UserVo userVo = userPoToUserVo(userPo);
        userVo.setTaken(taken);
        return userVo;
    }

    public UserVo getUserInfoByUserId(String taken) {
        int userId = JavaWebToken.getUserIdAndVerifyTakenFromTaken(taken);
        UserPo user = userDao.findUserByUserId(userId);
        Objects.requireNonNull(user, RESOURCE_NOT_FOUND);
        return userPoToUserVo(user);
    }

    public void updateUser(UserPo user, String taken) {
        int userId = JavaWebToken.getUserIdAndVerifyTakenFromTaken(taken);
        user.setUserId(userId);
        checkUpdateUserParameters(user);
        try {
            if (!userDao.updateUser(user)) {
                throw new ServerException();
            }
        }catch (DataAccessException e){
            throw new ParameterException("用户名已经被使用过了哦");
        }
    }

    private void checkUpdateUserParameters(UserPo user) {
        if (user.getUserName() == null || user.getEmail() == null || user.getSex() == null) {
            throw new ParameterException(ExceptionMessage.PARAMETER_IS_NOT_FULL);
        }
        ServiceExceptionChecking.checkUserNameIsTooLong(user.getUserName());
        ServiceExceptionChecking.checkSexIsTrue(user.getSex());
    }

    public void updateAvatar(String taken, String avatarUrl) {
        int userId = JavaWebToken.getUserIdAndVerifyTakenFromTaken(taken);
        if (!userDao.updateAvatarByAvatarAndUserId(avatarUrl, userId)) {
            throw new ServerException();
        }
    }

    public void updatePasswordAndCheckVerifyCode(String newPassword, String taken, String verifyCode) {
        int userId = JavaWebToken.getUserIdAndVerifyTakenFromTaken(taken);
        UserPo user = userDao.findUserByUserId(userId);
        String phoneNumber = user.getPhoneNumber();
        checkVerifyCode(phoneNumber, verifyCode);

        if (newPassword.length() < 5) {
            throw new ParameterException(ExceptionMessage.PASSWORD_IS_TOO_SHORT);
        }
        if (!userDao.updatePasswordByNewPasswordAndUserId(newPassword, userId)) {
            throw new ServerException();
        }
    }

    private void checkVerifyCode(String phoneNumber, String code) {
        String verifyCode = redisVerifyCode.getVerifyCode(phoneNumber);
        if (verifyCode == null) {
            throw new ParameterException("请重新获取验证码");
        }
        if (!code.equals(verifyCode)) {
            throw new VerifyCodeException();
        }
        redisVerifyCode.deleteVerifyCode(phoneNumber);
    }

    public String sendSms(String phoneNumber) throws ClientException {
        String code = RamdomNumberUtil.makeCode();
        redisVerifyCode.addVerifyCode(phoneNumber, code);
        SendSmsResponse sendSmsResponse = Sms.sendSms(phoneNumber, code);

        if (sendSmsResponse.getCode() == null || !sendSmsResponse.getCode().equals(SMS_SUCCESS_CODE)) {
            throw new SendSmsFailedException();
        }
        return code;
    }

    public void updatePersonId(String token, String personId){
        int userId = JavaWebToken.getUserIdAndVerifyTakenFromTaken(token);
        if(!userDao.updatePersonId(userId, personId)){
            throw new ServerException();
        }
    }

    private UserVo userPoToUserVo(UserPo userPo) {
        UserVo userVo = new UserVo();
        userVo.setUserId(userPo.getUserId());
        userVo.setCredibility(userPo.getCredibility());
        userVo.setUseTime(userPo.getUseTime());
        userVo.setSex(userPo.getSex());
        userVo.setPersonId(userPo.getPersonId());
        if (userPo.getUserName() != null) {
            userVo.setUserName(userPo.getUserName());
        }
        if (userPo.getEmail() != null) {
            userVo.setEmail(userPo.getEmail());
        }
        if (userPo.getPhoneNumber() != null) {
            userVo.setPhoneNumber(userPo.getPhoneNumber());
        }
        if (userPo.getImage() != null) {
            userVo.setImage(userPo.getImage());
        }
        if (userPo.getTrueName() != null) {
            userVo.setTrueName(userPo.getTrueName());
        }
        return userVo;
    }
}
