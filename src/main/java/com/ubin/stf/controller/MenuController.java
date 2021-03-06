package com.ubin.stf.controller;

import com.ubin.stf.model.Menu;
import com.ubin.stf.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/system/config")
public class MenuController {
    @Autowired
    MenuService menuService;

    @GetMapping("/menu")
    public List<Menu> getAllMenuWithChildrenUnderRole(){
        List<Menu> allMenuWithChildren = menuService.getAllMenuWithChildren(1);
        List<Integer> allMenuIdUnderAdmin = menuService.getAllMenuIdUnderAdmin();
        List<Menu> AllMenuWithChildrenUnderAdmin = menuService.getAllMenuWithChildrenUnderAdmin(allMenuWithChildren,allMenuIdUnderAdmin);
        return AllMenuWithChildrenUnderAdmin;

    }
}
