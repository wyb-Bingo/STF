package com.ubin.stf.controller;

import com.ubin.stf.model.Admin;
import com.ubin.stf.model.User;
import com.ubin.stf.service.AdminService;
import com.ubin.stf.utils.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    @Autowired
    AdminService adminService;

    @GetMapping("/admin/info")
    public ResponseBean getAdminInfo(){
        User admin = adminService.getAdminInfo();
        if (admin != null){
            return ResponseBean.ok("获取用户信息成功",admin);
        }
        return ResponseBean.errorAuth("获取用户信息失败");
    }
}
