package com.ubin.stf.mapper;

import com.ubin.stf.model.Team;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeamMapper {
    List<Team> getAdminIsTeamList(Integer id) ;

    void createTeam(Team team);

    void insertTeamAdmin(@Param("teamId") Integer teamId,@Param("adminId") Integer adminId);

    void insertTeamUser(@Param("teamId")Integer teamId,@Param("userId") Integer userId);

    List<Team> getUserOfTeamList(Integer id);
}
