package com.ubin.stf.mapper;

import com.ubin.stf.model.Attendance;
import org.apache.ibatis.annotations.Param;

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
}
