package com.minibox.dao.db;

import com.minibox.po.UserPo;
import com.minibox.po.VerifyCodePo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author MEI
 */
@Repository
public interface UserMapper {

    boolean insertUser(UserPo user);

    UserPo findUserByPhoneNumber(String phoneNumber);

    UserPo findUserByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

    UserPo findUserByUserId(int userId);

    UserPo findUserByUserName(String userName);

    UserPo findUserByPhoneNumberAndPassword(@Param("phoneNumber") String phoneNumber, @Param("password") String password);

    boolean updateUser(UserPo user);

    int increaseUserCredibilityPerWeek();

    boolean updateUseTime(int userId);

    boolean updateAvatarByAvatarAndUserId(@Param("avatar") String avatar, @Param("userId") int userId);

    boolean updatePasswordByNewPasswordAndUserId(@Param("newPassword") String newPassword, @Param("userId") int userId);

    boolean updateTakenByTakenAndUserId(@Param("taken") String taken, @Param("userId") int userId);

    boolean updatePersonId(@Param("userId") int userId, @Param("personId") String personId);

    boolean insertVerifyCode(VerifyCodePo verifyCode);

    VerifyCodePo findVerifyCode(String phoneNumber);
}
