package com.ubin.stf.service;

import com.ubin.stf.mapper.AdminMapper;
import com.ubin.stf.model.Admin;
import com.ubin.stf.model.Team;
import com.ubin.stf.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    TeamService teamService;

    @Autowired
    AdminMapper adminMapper;

    public Boolean setAdminTeam(Integer teamId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        if (user == null){
            return false;
        }
        List<Team> adminIsTeamList =  teamService.getAdminIsTeamList(user.getId());
        for (Team team:adminIsTeamList){
            if(team.getId().equals(teamId)){
                user.setTeamId(teamId);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, authentication.getCredentials(), authentication.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                return true;
            }
        }
        return false;
    }

    public Admin getAdminWithTeam() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Admin adminWithTeam = adminMapper.getAdminWithTeam(principal.getId());
        if(adminWithTeam == null){
            return null;
        }
        adminWithTeam.setUser(principal);
        return adminWithTeam;
    }

    public User getAdminInfo() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        principal.setPassword(null);
        principal.setOpenId(null);
        return principal;
    }

    public int insertAdminIfNoExists(Integer userId){
        return adminMapper.insertAdminIfNoExists(userId);
    }
}
