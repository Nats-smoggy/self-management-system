package com.example.self_management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.self_management.entity.Plan;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PlanMapper extends BaseMapper<Plan> {
    // 继承了 BaseMapper，增删改查全自动搞定！一行代码都不用写！
}