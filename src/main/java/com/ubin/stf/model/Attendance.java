package com.ubin.stf.model;

import java.util.List;

public class Attendance {
    private Integer id;
    private String name;
    private List<User> leader;
    private List<Department> departments;
    private List<WeekClazz> weekClazz;
    private String leaderStr;
    private String departmentStr;
    private String weekClazzStr;
    private Integer admin;
    private Boolean enabled;
    private String address;
    private WeekClazz today;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getLeader() {
        return leader;
    }

    public void setLeader(List<User> leader) {
        this.leader = leader;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public List<WeekClazz> getWeekClazz() {
        return weekClazz;
    }

    public void setWeekClazz(List<WeekClazz> weekClazz) {
        this.weekClazz = weekClazz;
    }

    public String getLeaderStr() {
        return leaderStr;
    }

    public void setLeaderStr(String leaderStr) {
        this.leaderStr = leaderStr;
    }

    public String getDepartmentStr() {
        return departmentStr;
    }

    public void setDepartmentStr(String departmentStr) {
        this.departmentStr = departmentStr;
    }

    public String getWeekClazzStr() {
        return weekClazzStr;
    }

    public void setWeekClazzStr(String weekClazzStr) {
        this.weekClazzStr = weekClazzStr;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public WeekClazz getToday() {
        return today;
    }

    public void setToday(WeekClazz today) {
        this.today = today;
    }

    public Integer getAdmin() {
        return admin;
    }

    public void setAdmin(Integer admin) {
        this.admin = admin;
    }
}
