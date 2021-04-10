package com.ubin.stf.model;

public class AttendanceData {
    private FormInline form;
    private StatisticsData[] trueData;
    private StatisticsData[] falseData;

    public FormInline getForm() {
        return form;
    }

    public void setForm(FormInline form) {
        this.form = form;
    }

    public StatisticsData[] getTrueData() {
        return trueData;
    }

    public void setTrueData(StatisticsData[] trueData) {
        this.trueData = trueData;
    }

    public StatisticsData[] getFalseData() {
        return falseData;
    }

    public void setFalseData(StatisticsData[] falseData) {
        this.falseData = falseData;
    }
}
