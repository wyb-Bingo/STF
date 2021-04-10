package com.ubin.stf.mapper;

import com.ubin.stf.model.Department;
import com.ubin.stf.model.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface DepartmentMapper {
    Department selectByPrimaryKey(Integer id);

    int insertSelective(Department department);

    int updateDepartmentIsDepPath(@Param("depPath") String s ,@Param("id") Integer id);

    int insertDepartmentAdmin(@Param("departmentId") Integer departmentId,@Param("adminId") Integer adminId);

    int insertDepartmentUser(@Param("departmentId") Integer departmentId,@Param("userId") Integer userId);

    List<Department> getAllDepartmentWithChildren(@Param("teamId") Integer teamId, @Param("rootId") Integer rootId);

    List<Department> getAllDepartmentWithAdmin(Integer teamId);

    int updateDepartmentIsParent(Integer id);

    List<User> getDepartmentIsAdmin(Integer id);

    int deleteByPrimaryKey(Integer id);

    int deleteDepAdminByDepId(Integer id);

    List<Department> getDepartmentIsChildren(Integer parentId);

    void setDepartmentIsNotParent(Integer parentId);

    Department getSimpleDepartmentInfoById(Integer depId);

    Department getUserIsDepartment(@Param("userId") Integer principalId,@Param("teamId") Integer teamId);
}
