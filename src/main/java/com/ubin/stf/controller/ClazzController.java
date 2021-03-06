package com.ubin.stf.controller;

import com.ubin.stf.model.Clazz;
import com.ubin.stf.service.ClazzService;
import com.ubin.stf.utils.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/work/attendance/setting")
public class ClazzController {
    @Autowired
    ClazzService clazzService;

    @PostMapping("/clazz")
    public ResponseBean createClazzUnderTeam(@RequestBody Clazz clazz){
        if (clazzService.addClazzUnderTeam(clazz) == 1){
            return ResponseBean.ok("创建成功");
        }
        return ResponseBean.ok("创建失败");
    }

}
