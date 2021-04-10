package com.ubin.stf.model;

public enum AttendanceStatusEnum {
    //若未签到，当前时间在开始考勤前返回
    INFO("未到时间","info",false),
    //若未签到，当前时间在开始考勤后且在结束考勤前则返回
    WAITING("点击签到","waiting",true),
    //直接查库，判断当前用户是否今天已经完成当前考勤组的签到
    SUCCESS("签到成功","success",false),
    //若未签到，判断当前时间是否超过结束考勤时间
    WARN("已超时","warn",false);

    private String statusText;
    private String statusIcon;
    private Boolean enabled;

    private AttendanceStatusEnum(String statusText, String statusIcon, Boolean enabled) {
        this.statusText = statusText;
        this.statusIcon = statusIcon;
        this.enabled  = enabled;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getStatusIcon() {
        return statusIcon;
    }

    public void setStatusIcon(String statusIcon) {
        this.statusIcon = statusIcon;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
