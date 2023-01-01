package com.donglin.yygh.common.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class R {

    private Integer code;
    private Boolean success;
    private String message;
    private Map<String,Object> data = new HashMap<>();


    private R(){

    }

    public static R ok(){
        R r = new R();
        r.setCode(REnum.SUCCESS.getCode());
        r.setSuccess(REnum.SUCCESS.getFlag());
        r.setMessage(REnum.SUCCESS.getMessage());
        return r;
    }

    public static R error(){
        R r = new R();
        r.setCode(REnum.ERROR.getCode());
        r.setSuccess(REnum.ERROR.getFlag());
        r.setMessage(REnum.ERROR.getMessage());
        return r;
    }

    public R code(Integer code){
        this.code = code;
        return this;
    }

    public R success(Boolean success){
        this.success = success;
        return this;
    }

    public R message(String message){
        this.message = message;
        return this;
    }

    public R data(String key,Object value){
        this.data.put(key,value);
        return this;
    }

    public R data(Map<String,Object> map){
        this.data = map;
        return this;
    }






}
