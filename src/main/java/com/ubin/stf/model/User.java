package com.ubin.stf.model;

import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User implements UserDetails {
    private Integer id;
    private String openId;
    private String name;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String address;
    private String studentNumber;
    private String userFace;
    private String remark;
    private Boolean enabled;
    private Integer teamId;
    private List<Role> roleList;
    private Department userIsDepartmentUnderTeam;
//    private Boolean accountNonExpired;
//    private Boolean accountNonLocked;
//    private Boolean credentialsNonExpired;
//    private  Collection<? extends GrantedAuthority> authorities;


    public Department getUserIsDepartmentUnderTeam() {
        return userIsDepartmentUnderTeam;
    }

    public void setUserIsDepartmentUnderTeam(Department userIsDepartmentUnderTeam) {
        this.userIsDepartmentUnderTeam = userIsDepartmentUnderTeam;
    }

    @java.beans.Transient
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
//        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_LOGIN");
//        authorityList.add(simpleGrantedAuthority);
        for (Role role:roleList){
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getName());
            authorityList.add(authority);
        }
        return authorityList;
    }


    @java.beans.Transient
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @java.beans.Transient
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @java.beans.Transient
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getUserFace() {
        return userFace;
    }

    public void setUserFace(String userFace) {
        this.userFace = userFace;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}
