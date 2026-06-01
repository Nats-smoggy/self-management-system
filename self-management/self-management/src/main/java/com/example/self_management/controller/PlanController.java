package com.example.self_management.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.self_management.common.Result;
import com.example.self_management.entity.Plan;
import com.example.self_management.service.IPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plan")
@CrossOrigin // 允许跨域请求
public class PlanController {

    @Autowired
    private IPlanService planService;

    // 🌟 1. 新增：专为 ECharts 大屏提供真实统计数据的核心接口！
    @GetMapping("/stats")
    public Result stats(@RequestParam Integer userId) {
        QueryWrapper<Plan> queryWrapper = new QueryWrapper<>();
        // 核心魔法：利用 select AS 语法，直接把数据库字段映射成 ECharts 需要的 name 和 value
        queryWrapper.select("status as name", "count(*) as value")
                .eq("user_id", userId)
                .groupBy("status");

        // listMaps 会直接返回形如 [{"name":"已完成", "value":12}, {"name":"未开始", "value":3}] 的 JSON 数组
        List<Map<String, Object>> list = planService.listMaps(queryWrapper);
        return Result.success(list);
    }

    // --- 以下是你原有的标准增删改查接口 ---

    @PostMapping("/add")
    public Result add(@RequestBody Plan plan) {
        planService.save(plan);
        return Result.success("计划添加成功！");
    }

    @PutMapping("/update")
    public Result update(@RequestBody Plan plan) {
        planService.updateById(plan);
        return Result.success("计划状态更新成功！");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        planService.removeById(id);
        return Result.success("计划删除成功！");
    }

    @GetMapping("/list")
    public Result list(@RequestParam Integer userId) {
        QueryWrapper<Plan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return Result.success(planService.list(queryWrapper));
    }
}