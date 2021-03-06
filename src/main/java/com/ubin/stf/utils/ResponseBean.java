package com.ubin.stf.utils;



public class ResponseBean {
    private int statusCode;
    private String msg;
    private Object content;

    private ResponseBean(int statusCode, String msg, Object content) {
        this.statusCode = statusCode;
        this.msg = msg;
        this.content = content;
    }

    public static ResponseBean ok(String msg){
        return new ResponseBean(200,msg,null);
    }
    public static ResponseBean ok(String msg,Object content){
        return new ResponseBean(200,msg,content);
    }
    public static ResponseBean error(String msg){
        return new ResponseBean(500,msg,null);
    }
    public static ResponseBean error(String msg,Object content){
        return new ResponseBean(500,msg,content);
    }
    public static ResponseBean errorAuth(String msg){
        return new ResponseBean(401,msg,null);
    }
    public static ResponseBean errorAuth(String msg,Object content){
        return new ResponseBean(401,msg,content);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }


}
