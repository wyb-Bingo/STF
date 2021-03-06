package com.ubin.stf.service;

import com.ubin.stf.mapper.TeamMapper;
import com.ubin.stf.mapper.UserMapper;
import com.ubin.stf.model.Team;
import com.ubin.stf.model.User;
import com.ubin.stf.utils.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    DepartmentService departmentService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userMapper.loadUserByUsername(s);
        if(user == null){
            throw new UsernameNotFoundException("用户名不存在");
        }
        user.setRoleList(roleService.getAdminIsRoleList(user.getId()));
        return user;

    }

    public UserDetails loadUserByOpenId(String openId){
        User user = userMapper.loadUserByOpenId(openId);
        user.setRoleList(roleService.getAdminIsRoleList(user.getId()));
        return user;
    }

    public User getSimpleUserInfoById(Integer id){
        return userMapper.getSimpleUserInfoById(id);
    }

    public void updateUserIsOpenId(User userWithOpenId){
        userMapper.updateUserIsOpenId(userWithOpenId);
    }


    public List<User> getDepartmentListIsUser(Integer[] ids) {
        List<User> result = new ArrayList<>();
        for(Integer id:ids){
            result.addAll(userMapper.getDepartmentIsUser(id)) ;
        }
        return result;
    }
    public List<User> getDepartmentIsUser(Integer id) {
        return userMapper.getDepartmentIsUser(id);
    }


    /**
     * user（name,studentNumber）
     * 判断学号是否在user表中存在，若存在则无需插入user表，因为此用户已经存在系统中
     * 判断此用户是否在team_user表中，若存在，则直接return，同时返回提示“此成员已在团队中，若要为此部门添加该成员，请执行<变更部门>操作”
     * 然后将user与team关联，插入team_user表中
     * 然后将user与department关联，首先插入userId和depRootId,然后插入userId与depId
     * @return
     */
    @Transactional
    public ResponseBean addUserUnderDepartment(User user, Integer depId){
       User selectUser = userMapper.selectByStudentNumber(user.getStudentNumber());
       User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (selectUser == null){
            user.setPassword(passwordEncoder.encode("123456"));
            user.setTeamId(principal.getTeamId());
            user.setUsername(user.getStudentNumber());
            user.setEnabled(true);
            userMapper.insertSelective(user);
            selectUser = user;
        }
        Integer userId = selectUser.getId();
        Integer teamId= principal.getTeamId();
        if (userMapper.selectCountTeamUserByUserIdAndTeamId(teamId,userId)>0){
            return ResponseBean.error("此成员已在团队中，若要为此部门添加该成员，请执行<变更部门>操作");
        }
        userMapper.insertTeamUser(teamId,userId);
        //departmentService.insertDepartmentUser(depRootId,userId);
        departmentService.insertDepartmentUser(depId,userId);
        return ResponseBean.ok("添加成功");
    }


    /**
     * 检查不能将自己操作离职 判断userId == adminId
     * 检查该名成员是否为这个team的管理员，如果为管理员，则提示“该成员具有管理员权限，请先将其权限移除，再进行离职操作"
     * 检查team_admin
     * 首先在team_user表中删除userId = userId，teamId = teamId
     * 然后在department_user表中删除depRootId,depId;
     * @param userId
     * @param depRootId
     * @param depId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserUnderDepartment(Integer userId, Integer depId) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer principalId = principal.getId();
        Integer teamId = principal.getTeamId();
        if (principalId.equals(userId)){
            return false;
        }
        if (userMapper.selectCountTeamAdminByTeamIdUserId(teamId,userId)>0){
            return false;
        }
        if(userMapper.deleteUserUnderTeam(teamId,userId) == 1){
            return userMapper.deleteUserUnderDepartment(userId, depId) > 0;
        }
        return false;
    }
}
