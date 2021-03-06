package com.ubin.stf.service;

import com.sun.xml.bind.v2.schemagen.xmlschema.AttributeType;
import com.ubin.stf.mapper.AttendanceMapper;
import com.ubin.stf.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AttendanceService {
    @Autowired
    DepartmentService departmentService;

    @Autowired
    UserService userService;

    @Autowired
    WeekClazzService weekClazzService;

    @Autowired
    AttendanceMapper attendanceMapper;

    @Autowired
    AdminService adminService;

    @Autowired
    RoleService roleService;

    @Autowired
    TeamService teamService;

    public List<AttendanceLeaderEntity> getAllDepartmentWithChildrenAndUser() {
        List<Department> allDepartmentWithChildren = departmentService.getAllDepartmentWithChildren();
        return recur(allDepartmentWithChildren);

    }

    public List<AttendanceLeaderEntity> recur(List<Department> allDepartmentWithChildren){
        if (allDepartmentWithChildren==null || allDepartmentWithChildren.size() == 0){
            return new ArrayList<>();
        }
        List<AttendanceLeaderEntity> result = new ArrayList<>();
        for (Department department:allDepartmentWithChildren){
            AttendanceLeaderEntity leaderEntity = new AttendanceLeaderEntity();
            leaderEntity.setLabel(department.getName());

            List<AttendanceLeaderEntity> recur = recur(department.getChildren());
            List<User> departmentIsUser = userService.getDepartmentIsUser(department.getId());
            if (departmentIsUser!=null && !departmentIsUser.isEmpty()){
                for (User user: departmentIsUser){
                    AttendanceLeaderEntity attendanceLeaderEntity = new AttendanceLeaderEntity();
                    attendanceLeaderEntity.setLabel(user.getName());
                    attendanceLeaderEntity.setChildren(null);
                    user.setRoleList(null);
                    user.setUserIsDepartmentUnderTeam(null);
                    attendanceLeaderEntity.setValue(user);
                    recur.add(attendanceLeaderEntity);
                }
            }


            leaderEntity.setChildren(recur);
            department.setChildren(null);
            department.setAdminList(null);
            leaderEntity.setValue(department);
            result.add(leaderEntity);
        }
        return result;
    }

    public List<AttendanceLeaderEntity> getAllDepartmentWithChildren() {
        List<Department> allDepartmentWithChildren = departmentService.getAllDepartmentWithChildren();
        return recur_recur(allDepartmentWithChildren);

    }
    private List<AttendanceLeaderEntity> recur_recur(List<Department> allDepartmentWithChildren){
        if (allDepartmentWithChildren==null || allDepartmentWithChildren.size() == 0){
            return new ArrayList<>();
        }
        List<AttendanceLeaderEntity> result = new ArrayList<>();
        for (Department department:allDepartmentWithChildren){
            AttendanceLeaderEntity leaderEntity = new AttendanceLeaderEntity();
            leaderEntity.setLabel(department.getName());
            leaderEntity.setChildren(recur_recur(department.getChildren()));
            department.setChildren(null);
            department.setAdminList(null);
            leaderEntity.setValue(department);
            result.add(leaderEntity);
        }
        return result;
    }

    //生成考勤组
    //先将weekClazz一条一条的插入，返回得到id，组成ids
    //再将leader中的id，抽出来，组成ids
    //再将departmens中的id，抽出来，组成ids
    //再获取userId和teamId
    //插入attendance表
    //再将leader中的user设置为考勤组负责人
    //将user插入admin表(判断是否存在)
    //维护admin_role表（查找当前团队考勤负责人的roleId）
    //维护team_admin表(设置user为当前团队的负责人)
    @Transactional(rollbackFor = Exception.class)
    public int createAttendanceWithColume(Attendance attendance){
        List<WeekClazz> weekClazz = attendance.getWeekClazz();
        List<User> leader = attendance.getLeader();
        List<Department> departments = attendance.getDepartments();
        if (weekClazz == null || weekClazz.size() == 0 ||
                leader == null ||leader.size() == 0 ||
                departments == null || departments.size() == 0){
            return 0;
        }

        List<Integer> weekClazzIds = weekClazzService.BatchInsertWeekClazz(weekClazz);
        List<Integer> leaderIds = new ArrayList<>();
        for (User user : leader){
            leaderIds.add(user.getId());
        }
        List<Integer> departmentIds = new ArrayList<>();
        for (Department department : departments){
            departmentIds.add(department.getId());
        }

        StringBuffer stringBuffer = new StringBuffer();
        for (Integer integer : weekClazzIds){
            stringBuffer.append(integer);
            stringBuffer.append(".");
        }
        String weekClazzStr = stringBuffer.substring(0, stringBuffer.length() - 1);
        stringBuffer.delete(0,stringBuffer.length());
        for (Integer integer : leaderIds){
            stringBuffer.append(integer);
            stringBuffer.append(".");
        }
        String leaderStr = stringBuffer.substring(0,stringBuffer.length()-1);
        stringBuffer.delete(0,stringBuffer.length());
        for (Integer integer : departmentIds){
            stringBuffer.append(integer);
            stringBuffer.append(".");
        }
        String departmentStr = stringBuffer.substring(0,stringBuffer.length()-1);
        stringBuffer.delete(0,stringBuffer.length());

        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer adminId = principal.getId();
        Integer teamId = principal.getTeamId();

        String roleName = "ROLE_"+teamId+"_ATTENDANCELEADER";
        int targetRoleIdUnderTeam = roleService.findTargetRoleIdUnderTeam(roleName, teamId);
        for (Integer integer : leaderIds){
            //同样要判断是否已经存在，因为可能已经在别的考勤组已经设置过考勤负责人
            roleService.insertRoleAdmin(targetRoleIdUnderTeam,integer);
            adminService.insertAdminIfNoExists(integer);
            teamService.insertTeamAdminIfNotExsit(teamId,integer);
        }


        return attendanceMapper.insertAttendance(attendance.getName(),weekClazzStr,leaderStr,departmentStr,adminId,teamId);


    }

    public List<Attendance> getAttendanceList() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer teamId = principal.getTeamId();

        List<Attendance> res = attendanceMapper.getAttendanceListByTeamId(teamId);

        for (Attendance attendance : res){
            //获取weekClazz
            List<WeekClazz> weekClazzList = new ArrayList<>();
            String weekClazzStr = attendance.getWeekClazzStr();
            String[] split = weekClazzStr.split("\\.");
            for (String s : split){
                WeekClazz weekClazz = weekClazzService.getWeekClazzById(Integer.valueOf(s));
                weekClazzList.add(weekClazz);
            }
            attendance.setWeekClazz(weekClazzList);

            //获取Leader
            List<User> leaderList = new ArrayList<>();
            String leaderStr = attendance.getLeaderStr();
            String[] split1 = leaderStr.split("\\.");
            for (String s : split1){
                User simpleUserInfoById = userService.getSimpleUserInfoById(Integer.valueOf(s));
                leaderList.add(simpleUserInfoById);
            }
            attendance.setLeader(leaderList);

            //获取department
            List<Department> departmentList = new ArrayList<>();
            String departmentStr = attendance.getDepartmentStr();
            String[] split2 = departmentStr.split("\\.");
            for (String s : split2){
                Department simpleDepartmentInfoById = departmentService.getSimpleDepartmentInfoById(Integer.valueOf(s));
                departmentList.add(simpleDepartmentInfoById);
            }
            attendance.setDepartments(departmentList);
        }
        return res;
    }

    public int setAttendanceOfEnabled(Boolean value,Integer id) {
        return attendanceMapper.setAttendanceOfEnabled(value,id);
    }
}
