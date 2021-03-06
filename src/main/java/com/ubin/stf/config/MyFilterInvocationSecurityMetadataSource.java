package com.ubin.stf.config;

import com.ubin.stf.model.Menu;
import com.ubin.stf.model.Role;
import com.ubin.stf.model.User;
import com.ubin.stf.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

/**
 * @author 70432
 */
@Component
public class MyFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private MenuService menuService;

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
        Integer teamId = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTeamId();
        if (teamId == null){
            return SecurityConfig.createList("ROLE_ACCESS_DENY");
        }
        List<Menu> menuList = menuService.getAllMenuListWithRole(teamId);
        for (Menu menu:menuList){
            if (antPathMatcher.match(menu.getUrl(),requestUrl) && menu.getRoleList().size() >0){
                /*
                String[] attributeNames = menu.getRoleList.stream().map(r -> r.getName()).toArray(String[]::new);
                 return SecurityConfig.createList(attributeNames);
                 */
                List<Role> roleList = menu.getRoleList();
                String[] attributeNames = new String[roleList.size()];
               for (int i=0;i<roleList.size();i++){
                   attributeNames[i] = roleList.get(i).getName();
               }
                return SecurityConfig.createList(attributeNames);
            }
        }
        return SecurityConfig.createList("ROLE_ACCESS_DENY");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
