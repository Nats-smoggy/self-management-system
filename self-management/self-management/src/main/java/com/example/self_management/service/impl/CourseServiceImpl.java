package com.example.self_management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.self_management.entity.Course;
import com.example.self_management.mapper.CourseMapper;
import com.example.self_management.service.ICourseService;
import org.springframework.stereotype.Service;

@Service // 🌟 同样必须要有！
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {
}