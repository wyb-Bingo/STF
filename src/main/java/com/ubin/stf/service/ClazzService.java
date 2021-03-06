package com.ubin.stf.service;

import com.ubin.stf.mapper.ClazzMapper;
import com.ubin.stf.model.Clazz;
import com.ubin.stf.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClazzService {

    @Autowired
    ClazzMapper clazzMapper;

    public List<Clazz> getAllClazzList() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer teamId = principal.getTeamId();
        return clazzMapper.getAllClazzList(teamId);
    }

    public int addClazzUnderTeam(Clazz clazz) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer teamId = principal.getTeamId();
        clazz.setTeamId(teamId);
        return clazzMapper.insertClazzUnderTeam(clazz);
    }
}
