package com.example.self_management.entity;

import com.alibaba.excel.annotation.ExcelProperty; // 🌟 引入 EasyExcel
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("sys_course")
public class Course {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    @ExcelProperty("课程名称") // 🌟 对应 Excel 里的第一列表头
    private String courseName;

    @ExcelProperty("星期几")   // 🌟 对应 Excel 里的第二列表头 (填数字1-7)
    private Integer dayOfWeek;

    @ExcelProperty("第几节")   // 🌟 对应 Excel 里的第三列表头 (填数字1-10)
    private Integer section;

    @ExcelProperty("上课地点") // 🌟 对应 Excel 里的第四列表头
    private String location;

    @ExcelProperty("任课教师") // 🌟 对应 Excel 里的第五列表头
    private String teacher;

    // --- 下面的 Getters 和 Setters 保持你原来的不变即可 ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public Integer getSection() { return section; }
    public void setSection(Integer section) { this.section = section; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }
}