package com.ubin.stf.mapper;

import com.ubin.stf.model.Attendance;
import com.ubin.stf.model.AttendanceUser;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AttendanceMapper {
    int setAttendanceOfEnabled(@Param("enabled") Boolean value,@Param("id") Integer id);

    int insertAttendance(@Param("name") String name,
                         @Param("weekClazzStr") String weekClazzStr,
                         @Param("leaderStr") String leaderStr,
                         @Param("departmentStr") String departmentStr,
                         @Param("adminId") Integer adminId,
                         @Param("teamId") Integer teamId);

    List<Attendance> getAttendanceListByTeamId(Integer teamId);

    int updateAddress(@Param("address") String address,@Param("id") Integer id);

    int getAttendanceToUserByIdAndData(@Param("attendanceId") Integer id,@Param("userId") Integer id1,@Param("dateStr") String dateStr);

    Attendance getAttendanceInfoById(Integer id);

    void insertAttendanceUser(@Param("attendanceId") Integer attendanceId,@Param("userId") Integer id,@Param("date") LocalDate now,@Param("time") LocalTime now1,@Param("isLeave") int i);

     List<AttendanceUser> getAttendanceWithUserByIdAndData(@Param("attendanceId")Integer id,@Param("date") String date);

    List<AttendanceUser> getAttendaceToUserByIdAndFromToDate(@Param("userId") Integer userId,@Param("attendanceId") Integer id,@Param("dateFrom") String dateFrom,@Param("dateTo") String dateTo);
}
