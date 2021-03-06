package com.ubin.stf.service;

import com.ubin.stf.mapper.MenuMapper;
import com.ubin.stf.model.Menu;
import com.ubin.stf.model.Role;
import com.ubin.stf.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuMapper menuMapper;

    public List<Menu> getAllMenuList() {
        return menuMapper.getAllMenuList();
    }

    public List<Menu> getAllMenuListWithRole(Integer teamId){
        return menuMapper.getAllMenuWithRole(teamId);
    }

    public List<Menu> getAllMenuWithChildren(Integer parentId){
        return menuMapper.getAllMenuWithChildren(parentId);
    }

    public List<Menu> getAllMenuWithChildrenUnderAdmin(List<Menu> menus,List<Integer> ids){
        List<Menu> result = new ArrayList<>();
        for(Menu menu:menus){
            if(ids.contains(menu.getId())){
                result.add(menu);
            }else{
                if(!menu.getSubMenuList().isEmpty()){
                    Menu item = menu;
                    item.setSubMenuList(getAllMenuWithChildrenUnderAdmin(item.getSubMenuList(),ids));
                    if (!item.getSubMenuList().isEmpty()){
                        result.add(item);
                    }
                }
            }
        }
        return result;
    }

    public List<Integer> getAllMenuIdUnderAdmin() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Role> roleList = principal.getRoleList();
        List<Integer> result = new ArrayList<>();
        Integer teamId = principal.getTeamId();
        for (Role role:roleList){
            if(!role.getTeamId().equals(teamId)){
                continue;
            }
           result.addAll(menuMapper.getAllMenuIdUnderAdmin(role.getId()));
        }
        return result;
    }
}
