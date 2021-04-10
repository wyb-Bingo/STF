package com.ubin.stf.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubin.stf.model.Admin;
import com.ubin.stf.model.Team;
import com.ubin.stf.model.User;
import com.ubin.stf.service.AdminService;
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
public class LoginController {
    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;

    @GetMapping("/adminTeam/set")
    public ResponseBean setAdminTeam(@RequestParam Integer teamId) throws JsonProcessingException {
        if (adminService.setAdminTeam(teamId)){
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String userDetail = new ObjectMapper().writeValueAsString(principal);
            String jwtToken = JwtUtils.createJwtToken(userDetail);
            Map<String,String> map = new HashMap<>();
            map.put("token",jwtToken);
            return ResponseBean.ok("选择成功", map);
        }
        return ResponseBean.errorAuth("选择失败");
    }

    @GetMapping("/adminTeam/show")
    public ResponseBean showAdminTeam(){
        Admin admin = adminService.getAdminWithTeam();
        if (admin == null){
            return ResponseBean.error("没有已管理的团队");
        }
        return ResponseBean.ok("查询成功",admin.getTeamList());
    }

    @GetMapping("/userTeam/show")
    public ResponseBean showUserTeam(){
        List<Team> teamList =  userService.getUserOfTeam();
        if (teamList == null){
            return ResponseBean.error("还未加入团队，请联系管理员加入");
        }
        return ResponseBean.ok("查询成功",teamList);
    }

    @GetMapping("/userTeam/set")
    public ResponseBean setUserTeam(@RequestParam Integer teamId) throws JsonProcessingException {
        if (userService.setUserTeam(teamId)) {
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String userDetail = new ObjectMapper().writeValueAsString(principal);
            String jwtToken = JwtUtils.createJwtToken(userDetail);
            Map<String,String> map = new HashMap<>();
            map.put("token",jwtToken);
            return ResponseBean.ok("选择成功", map);
        }
        return ResponseBean.errorAuth("选择失败");
    }


}
