package com.ubin.stf.service;

import com.ubin.stf.mapper.TeamMapper;
import com.ubin.stf.model.Department;
import com.ubin.stf.model.Role;
import com.ubin.stf.model.Team;
import com.ubin.stf.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {
    @Autowired
    TeamMapper teamMapper;

    @Autowired
    AdminService adminService;

    @Autowired
    RoleService roleService;

    @Autowired
    DepartmentService departmentService;

    public List<Team> getAdminIsTeamList(Integer id) {
        return teamMapper.getAdminIsTeamList(id);
    }

    /**
     * 先判断userId是否已经存在在admin表，若存在，则无需将Id插入admin
     * 在team表创建新团队，createAdminId = userId
     * 在team_admin，team_user表中插入
     * 在role中创建角色超级管理员，teamId = 上一步Id
     * 在admin_role中插入
     * 在department中创建新部门 = team
     * 在department_admin 与 department_user中插入
     * 在menu_role中赋予他全部的权限 6 7 8 9 10
     * @return
     */
    @Transactional
    public Boolean addTeamUnderAdmin(Team team) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer adminId = principal.getId();
        //1
        adminService.insertAdminIfNoExists(adminId);
        //2
        team.setCreateAdminId(adminId);
        teamMapper.createTeam(team);
        Integer teamId  = team.getId();
        //3
        teamMapper.insertTeamAdmin(teamId,adminId);
       // teamMapper.insertTeamUser(teamId,adminId);
        //4
        Role role = new Role();
        role.setName("ROLE_"+teamId+"_SUPERADMIN");
        role.setNameZh("超级管理员");
        role.setTeamId(teamId);
        roleService.createRole(role);
        Integer roleId = role.getId();
        //5
        roleService.insertRoleAdmin(roleId,adminId);
        //6
        Department department = new Department();
        department.setName(team.getTeamName());
        department.setParentId(0);
        department.setEnabled(true);
        department.setIsParent(false);
        department.setTeamId(teamId);
        departmentService.creatRootDepartment(department);
        Integer departmentId = department.getId();
        departmentService.updateDepartmentIsDepPath("."+department.getId(),departmentId);
        //7
        departmentService.insertDepartmentAdmin(departmentId,adminId);
        //departmentService.insertDepartmentUser(departmentId,adminId);
        //8
        Integer[] ids = new Integer[]{6,7,8,9,10};
        roleService.insertMenuListOfRole(ids,roleId);

        //创建团队的其他初始角色
        Role depLeader = new Role();
        depLeader.setName("ROLE_"+teamId+"_DEPLEADER");
        depLeader.setNameZh("部门负责人");
        depLeader.setTeamId(teamId);
        roleService.createRole(depLeader);
        Integer depLeaderId = depLeader.getId();
        Integer[] depLeaderIds = new Integer[]{6};
        roleService.insertMenuListOfRole(depLeaderIds,depLeaderId);

        Role attendanceAdmin = new Role();
        attendanceAdmin.setName("ROLE_"+teamId+"_ATTENDANCEADMIN");
        attendanceAdmin.setNameZh("考勤后台管理员");
        attendanceAdmin.setTeamId(teamId);
        roleService.createRole(attendanceAdmin);
        Integer attendanceAdminId = attendanceAdmin.getId();
        Integer[] attendanceAdminIds = new Integer[]{8};
        roleService.insertMenuListOfRole(attendanceAdminIds,attendanceAdminId);

        Role attendanceLeader = new Role();
        attendanceLeader.setName("ROLE_"+teamId+"_ATTENDANCELEADER");
        attendanceLeader.setNameZh("考勤组负责人");
        attendanceLeader.setTeamId(teamId);
        roleService.createRole(attendanceLeader);
        Integer attendanceLeaderId = attendanceLeader.getId();
        Integer[] attendanceLeaderIds = new Integer[]{14};
        roleService.insertMenuListOfRole(attendanceLeaderIds,attendanceLeaderId);

        return  true ;
    }

    public void insertTeamAdminIfNotExsit(Integer teamId,Integer adminId){
        teamMapper.insertTeamAdmin(teamId,adminId);
    }

}
