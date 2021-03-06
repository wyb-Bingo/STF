package com.ubin.stf.mapper;

import com.ubin.stf.model.Clazz;

import java.util.List;

public interface ClazzMapper {
    List<Clazz> getAllClazzList(Integer teamId);

    int insertClazzUnderTeam(Clazz clazz);
}
