package com.ubin.stf.mapper;

import com.ubin.stf.model.Team;
import com.ubin.stf.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 70432
 */
public interface UserMapper {
    User loadUserByUsername(String username);

    User loadUserByOpenId(String openId);

    int updateUserIsOpenId(User userWithOpenId);

    List<User> getDepartmentIsUser(Integer id);

    int insertSelective(User user);

    User selectByStudentNumber(String studentNumber );

    int selectCountTeamUserByUserIdAndTeamId(@Param("teamId") Integer teamId,@Param("userId") Integer userId);

    int insertTeamUser(@Param("teamId") Integer teamId,@Param("userId") Integer userId);

    int selectCountTeamAdminByTeamIdUserId(@Param("teamId") Integer teamId,@Param("adminId") Integer adminId);

    int deleteUserUnderTeam(@Param("teamId") Integer teamId,@Param("userId") Integer userId);

    int deleteUserUnderDepartment(@Param("userId") Integer userId,@Param("depId") Integer depId);

    User getSimpleUserInfoById(Integer id);

}
