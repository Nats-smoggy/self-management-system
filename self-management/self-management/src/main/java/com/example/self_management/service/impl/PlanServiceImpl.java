package com.example.self_management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.self_management.entity.Plan;
import com.example.self_management.mapper.PlanMapper;
import com.example.self_management.service.IPlanService;
import org.springframework.stereotype.Service;

@Service
public class PlanServiceImpl extends ServiceImpl<PlanMapper, Plan> implements IPlanService {
}