package com.ubin.stf.controller;

import com.ubin.stf.model.Attendance;
import com.ubin.stf.model.AttendanceLeaderEntity;
import com.ubin.stf.model.Clazz;
import com.ubin.stf.service.AttendanceService;
import com.ubin.stf.service.ClazzService;
import com.ubin.stf.utils.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/work/attendance")
public class AttendanceController {
    @Autowired
    AttendanceService attendanceService;

    @Autowired
    ClazzService clazzService;

    @GetMapping("/setting/leader")
    public List<AttendanceLeaderEntity> getAllDepartmentWithChildrenAndUser(){
        return attendanceService.getAllDepartmentWithChildrenAndUser();
    }

    @GetMapping("/setting/department")
    public List<AttendanceLeaderEntity> getAllDepartmentWithChildren(){
        return attendanceService.getAllDepartmentWithChildren();
    }

    @GetMapping("/setting/clazz")
    public List<Clazz> getAllClazzList(){
        return clazzService.getAllClazzList();
    }

    @PostMapping("/setting/attendance")
    public ResponseBean createAttendance(@RequestBody Attendance attendance){
        int attendanceWithColume = attendanceService.createAttendanceWithColume(attendance);
        if (attendanceWithColume == 0){
            return ResponseBean.error("创建失败");
        }
        return ResponseBean.ok("创建成功");
    }

    @GetMapping("/setting/attendance")
    public List<Attendance> getAttendanceList(){
        return attendanceService.getAttendanceList();
    }
    @PostMapping("/setting/enabled/{id}")
    public ResponseBean setAttendanceOfEnabled(@RequestBody boolean value, @PathVariable Integer id){
        int i = attendanceService.setAttendanceOfEnabled(value, id);
        if (i == 0){
            return ResponseBean.error("操作失败");
        }
        return ResponseBean.ok("操作成功");
    }




}
