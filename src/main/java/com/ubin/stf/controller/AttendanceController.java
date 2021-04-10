package com.ubin.stf.controller;

import com.ubin.stf.model.*;
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

    @GetMapping("/setting/attendance/user/show")
    public List<Attendance> showAttendance(){
        return attendanceService.showAttendance();
    }

    @PutMapping("/setting/attendance/admin/address/update")
    public ResponseBean updateAddressOfAttendance(@RequestBody Attendance attendance){
        int num = attendanceService.updateAddressOfAttendance(attendance.getAddress(),attendance.getId());
        if (num == 1){
            return ResponseBean.ok("设置成功");
        }
        return ResponseBean.error("设置失败");
    }

    @GetMapping("/setting/attendance/admin/get")
    public ResponseBean getAttendanceListOfAdmin(){
        List<Attendance> attendanceList = attendanceService.getAttendanceListOfAdmin();
        if (attendanceList.isEmpty()){
            return ResponseBean.error("获取失败");
        }
        return ResponseBean.ok("获取成功",attendanceList);
    }

    @GetMapping("/setting/attendance/user/status")
    public AttendanceStatusEnum getUserIsAttendanceStatus(@RequestParam Integer id){
        return attendanceService.getUserIsAttendanceStatus(id);
    }

    @PostMapping("/setting/attendance/user/click")
    public ResponseBean clickAttendance(@RequestBody UserLocaltion userLocaltion){
        String msg = attendanceService.insertAttendanceClickInfo(userLocaltion);
        return ResponseBean.ok(msg);
    }

    @GetMapping("/statistics/data")
    public AttendanceData getCurrentDateAttendanceData(@RequestParam String date,@RequestParam Integer id){
        return attendanceService.getCurrentDateAttendanceData(date,id);
    }

    @GetMapping("/statistics/data/user")
    public List<UserAttendanceData> getUserCurrentAttendanceData(@RequestParam Integer id,@RequestParam String date){
        return attendanceService.getUserCurrentAttendanceData(id,date);
    }



}
