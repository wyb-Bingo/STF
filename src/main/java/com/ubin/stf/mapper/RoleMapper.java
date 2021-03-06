package com.ubin.stf.mapper;

import com.ubin.stf.model.Role;
import com.ubin.stf.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper {
    List<Role> getAdminIsRoleList(Integer adminId);

    void createRole(Role role);

    //检查是否存在IFNOTEXSIT
    void insertRoleAdmin(@Param("rid") Integer roleId,@Param("adminId") Integer adminId);

    void insertMenuListOfRole(@Param("ids") Integer[] ids,@Param("roleId") Integer roleId);

    int findTargetRoleIdUnderTeam(@Param("roleName") String roleName,@Param("teamId") Integer teamId);
}
