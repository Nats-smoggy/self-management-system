package com.example.self_management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.self_management.entity.Course;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 🌟 同样加上护体！
public interface CourseMapper extends BaseMapper<Course> {
}