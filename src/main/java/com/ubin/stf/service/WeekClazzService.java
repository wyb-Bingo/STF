package com.ubin.stf.service;

import com.ubin.stf.mapper.WeekClazzMapper;
import com.ubin.stf.model.WeekClazz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeekClazzService {
    @Autowired
    WeekClazzMapper weekClazzMapper;

    public List<Integer> BatchInsertWeekClazz(List<WeekClazz> weekClazzes){
        List<Integer> ids = new ArrayList<>();
        for (WeekClazz weekClazz : weekClazzes){
            weekClazzMapper.insertWeekClazz(weekClazz);
            int id = weekClazz.getId();
            ids.add(id);
        }
        return ids;
    }

    public WeekClazz getWeekClazzById(Integer id) {
        return weekClazzMapper.getWeekClazzById(id);
    }
}
