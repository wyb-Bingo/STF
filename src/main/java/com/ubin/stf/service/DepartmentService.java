package com.ubin.stf.service;

import com.ubin.stf.mapper.DepartmentMapper;
import com.ubin.stf.model.Department;
import com.ubin.stf.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    DepartmentMapper departmentMapper;

    @Autowired
    UserService userService;

    public Department getSimpleDepartmentInfoById(Integer depId){
        return departmentMapper.getSimpleDepartmentInfoById(depId);
    }


    public void creatRootDepartment(Department department) {
        departmentMapper.insertSelective(department);
    }

    public void updateDepartmentIsDepPath(String s,Integer id) {
        departmentMapper.updateDepartmentIsDepPath(s,id);
    }

    public void insertDepartmentAdmin(Integer departmentId, Integer adminId) {
        departmentMapper.insertDepartmentAdmin(departmentId,adminId);
    }

    public void insertDepartmentUser(Integer departmentId, Integer userId) {
        departmentMapper.insertDepartmentUser(departmentId,userId);
    }

    public List<Department> getAllDepartmentWithChildren() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer teamId = principal.getTeamId();
        Integer adminId = principal.getId();
        List<Department> allDepartmentWithChildren = departmentMapper.getAllDepartmentWithChildren(teamId, 0);
        List<Department> recur = recur(allDepartmentWithChildren,adminId,false);
        return recur;
    }

    private List<Department> recur(List<Department> departmentList,Integer adminId,Boolean adminFlag){
        if (departmentList == null || departmentList.isEmpty()){
            return new ArrayList<>();
        }
        for (Department department:departmentList){
            List<User> adminList = department.getAdminList();
            for (User user:adminList){
                user.setOpenId("");
                user.setPassword("");
                if (user.getId().equals(adminId)){
                    department.setAdministrator(true);
                }
            }
            if (department.getAdministrator() == null || !department.getAdministrator()){
                    department.setAdministrator(adminFlag);
            }
            if (department.getChildren() != null && !department.getChildren().isEmpty()){
                department.setChildren(recur(department.getChildren(),adminId,department.getAdministrator()));
            }
        }
        return departmentList;

    }

    public List<Department> getAllDepartmentWithAdmin(){
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer teamId = principal.getTeamId();
        return departmentMapper.getAllDepartmentWithAdmin(teamId);
    }

    public List<User> getDepartmentIsAdminList(Integer depId){
        return departmentMapper.getDepartmentIsAdmin(depId);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addDepartmentUnderTeam(Department department) {
        Department newDepartment = new Department();
        newDepartment.setTeamId(department.getTeamId());
        newDepartment.setIsParent(false);
        newDepartment.setParentId(department.getId());
        newDepartment.setEnabled(true);
        newDepartment.setName(department.getName());
        String parentDepPath = department.getDepPath();
        if (departmentMapper.insertSelective(newDepartment) == 1){
            String depPath = parentDepPath+"."+newDepartment.getId();
            if (departmentMapper.updateDepartmentIsDepPath(depPath,newDepartment.getId()) == 1){
                if (!department.getIsParent()){
                    return departmentMapper.updateDepartmentIsParent(department.getId()) == 1;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * ????????????isParent=true,?????????????????????????????????????????????
     * ????????????????????????????????????????????????????????????????????????
     * ???????????????????????????department_user??????????????????????????????department???department_admin,?????????parent?????????????????????
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDepartmentUnderParent(Integer id){
        Department department = departmentMapper.selectByPrimaryKey(id);
        if (department.getIsParent()){
            return false;
        }
        List<User> userList = userService.getDepartmentIsUser(id);
        if (!userList.isEmpty()){
            return false;
        }
        if (departmentMapper.deleteByPrimaryKey(id) == 1 && departmentMapper.deleteDepAdminByDepId(id) >= 0){
            Integer parentId = department.getParentId();
            List<Department> departmentList = departmentMapper.getDepartmentIsChildren(parentId);
            if (departmentList.isEmpty()){
                departmentMapper.setDepartmentIsNotParent(parentId);
            }
            return true;
        }

        return false;
    }

    public Department getUserIsDepartment(Integer principalId,Integer teamId) {
        return departmentMapper.getUserIsDepartment(principalId,teamId);
    }
}
