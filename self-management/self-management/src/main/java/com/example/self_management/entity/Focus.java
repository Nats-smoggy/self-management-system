package com.example.self_management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("sys_focus")
public class Focus {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer duration;
    private String focusDate;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public String getFocusDate() { return focusDate; }
    public void setFocusDate(String focusDate) { this.focusDate = focusDate; }
}