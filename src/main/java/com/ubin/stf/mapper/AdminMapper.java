package com.ubin.stf.mapper;

import com.ubin.stf.model.Admin;

public interface AdminMapper {

    Admin getAdminWithTeam(Integer id);

    int insertAdminIfNoExists(Integer userId);
}
