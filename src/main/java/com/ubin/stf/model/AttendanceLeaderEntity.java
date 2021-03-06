package com.ubin.stf.model;

import java.util.List;

public class AttendanceLeaderEntity {
    private Object value;
    private String label;
    private List<AttendanceLeaderEntity> children;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<AttendanceLeaderEntity> getChildren() {
        return children;
    }

    public void setChildren(List<AttendanceLeaderEntity> children) {
        this.children = children;
    }
}
