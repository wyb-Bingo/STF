package com.ubin.stf.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubin.stf.model.Admin;
import com.ubin.stf.model.User;
import com.ubin.stf.service.AdminService;
import com.ubin.stf.service.UserService;
import com.ubin.stf.utils.JwtUtils;
import com.ubin.stf.utils.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 70432
 */
@RestController
public class LoginController {
    @Autowired
    AdminService adminService;

    @GetMapping("/adminTeam/set")
    public ResponseBean setAdminTeam(@RequestParam Integer teamId) throws JsonProcessingException {
        if (adminService.setAdminTeam(teamId)){
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String userDetail = new ObjectMapper().writeValueAsString(principal);
            String jwtToken = JwtUtils.createJwtToken(userDetail);
            Map<String,String> map = new HashMap<>();
            map.put("token",jwtToken);
            ResponseBean responseBean = ResponseBean.ok("选择成功", map);
           return responseBean;
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


}
