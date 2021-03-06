package com.ubin.stf.service;

import com.ubin.stf.mapper.RoleMapper;
import com.ubin.stf.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    RoleMapper roleMapper;

    public List<Role> getAdminIsRoleList(Integer adminId){
            return roleMapper.getAdminIsRoleList(adminId);
    }

    public void createRole(Role role) {
        roleMapper.createRole(role);
    }

    public void insertRoleAdmin(Integer roleId, Integer adminId) {
        roleMapper.insertRoleAdmin(roleId,adminId);
    }

    public void insertMenuListOfRole(Integer[] ids, Integer roleId) {
        roleMapper.insertMenuListOfRole(ids,roleId);
    }

    public int findTargetRoleIdUnderTeam(String roleName,Integer teamId){
        return roleMapper.findTargetRoleIdUnderTeam(roleName,teamId);
    }

}
