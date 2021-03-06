package com.ubin.stf.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubin.stf.model.Department;
import com.ubin.stf.model.Team;
import com.ubin.stf.model.User;
import com.ubin.stf.service.DepartmentService;
import com.ubin.stf.service.RoleService;
import com.ubin.stf.service.TeamService;
import com.ubin.stf.service.UserService;
import com.ubin.stf.utils.JwtUtils;
import com.ubin.stf.utils.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 70432
 */
@RestController
@RequestMapping("/team/build")
public class TeamBuildController {
    @Autowired
    TeamService teamService;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;
    @PostMapping("/")
    public ResponseBean addTeamUnderAdmin(@RequestBody Team team) throws JsonProcessingException {
        if(teamService.addTeamUnderAdmin(team)){
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            principal.setRoleList(roleService.getAdminIsRoleList(principal.getId()));
            String userDetail = new ObjectMapper().writeValueAsString(principal);
            String jwtToken = JwtUtils.createJwtToken(userDetail);
            Map<String,String> map = new HashMap<>();
            map.put("token",jwtToken);
            return ResponseBean.ok("创建成功",map);
        }
        return ResponseBean.error("创建失败");
    }

    @GetMapping("/department")
    public List<Department> getAllDepartmentWhitChildren(){
        return departmentService.getAllDepartmentWithChildren();
       // return departmentService.getAllDepartmentWithAdmin();
    }

    /**
     * department(name,parentId,depPath,isParent,teamId)
     * 更新父级目录的isParent
     * @return
     */
    @PostMapping("/department")
    public ResponseBean addDepartmentUnderTeam(@RequestBody Department department){
        if (!department.getAdministrator()){
            return ResponseBean.error("权限不足");
        }
        if(departmentService.addDepartmentUnderTeam(department)){
            return ResponseBean.ok("添加成功");
        }
        return ResponseBean.error("添加失败");
    }

    @DeleteMapping("/department/{id}")
    public ResponseBean deleteDepartmentUnder(@PathVariable Integer id){
        if (departmentService.deleteDepartmentUnderParent(id)){
            return ResponseBean.ok("删除成功");
        }
        return ResponseBean.error("删除失败,部门存在成员，请将部门下全部成员移除后再进行删除操作");
    }

    @GetMapping("/user")
    public List<User> getDepartmentIsUserList(@RequestParam Integer[] depIds){
        return userService.getDepartmentListIsUser(depIds);
    }

    @PostMapping("/user")
    public ResponseBean addUserUnderDepartment(@RequestBody User user,@RequestParam("depId") Integer depId ){
        return userService.addUserUnderDepartment(user,depId);
    }

    @DeleteMapping("/user")
    public ResponseBean deleteUserUnderDepartment(@RequestParam("userId") Integer userId,@RequestParam("depId") Integer depId){
        if(userService.deleteUserUnderDepartment(userId,depId)){
            return ResponseBean.ok("离职成功");
        }
        return ResponseBean.error("离职失败,该成员具有管理员权限，请先将其权限移除，再进行离职操作");
    }
}
