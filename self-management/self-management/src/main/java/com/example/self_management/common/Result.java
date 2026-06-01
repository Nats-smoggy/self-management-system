package com.example.self_management.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code; // 状态码：200代表成功，500代表失败
    private String msg;   // 提示信息：比如“操作成功”或“密码错误”
    private T data;       // 真正要装的数据（比如用户列表）

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // 成功时的快捷包装方法
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    public static Result success() {
        return success(null);
    }

    // 失败时的快捷包装方法
    public static Result error(String msg) {
        Result result = new Result();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}