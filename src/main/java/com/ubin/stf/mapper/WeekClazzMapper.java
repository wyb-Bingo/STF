package com.ubin.stf.mapper;

import com.ubin.stf.model.WeekClazz;
import org.apache.ibatis.annotations.Param;

public interface WeekClazzMapper {
    int insertWeekClazz(WeekClazz weekClazz);

    WeekClazz getWeekClazzById(Integer id);
}
