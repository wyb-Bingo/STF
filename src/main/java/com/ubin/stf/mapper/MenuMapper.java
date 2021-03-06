package com.ubin.stf.mapper;

import com.ubin.stf.model.Menu;

import java.util.Collection;
import java.util.List;

public interface MenuMapper {

    public List<Menu> getAllMenuList();
    List<Menu> getAllMenuWithRole(Integer teamId);

    List<Menu> getAllMenuWithChildren(Integer parentId);

    List<Integer> getAllMenuIdUnderAdmin(Integer id);
}
