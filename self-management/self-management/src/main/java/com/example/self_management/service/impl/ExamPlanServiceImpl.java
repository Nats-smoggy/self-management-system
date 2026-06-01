package com.example.self_management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.self_management.mapper.ExamPlanMapper;
import com.example.self_management.entity.ExamPlan;
import com.example.self_management.service.IExamPlanService;
import org.springframework.stereotype.Service;

// 👇 就是这行 @Service 最关键！如果没有它，Spring 就瞎了，根本看不见这个类！
@Service
public class ExamPlanServiceImpl extends ServiceImpl<ExamPlanMapper, ExamPlan> implements IExamPlanService {
}