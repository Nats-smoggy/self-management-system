package com.example.self_management.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.self_management.common.Result;
import com.example.self_management.entity.ExamPlan;
import com.example.self_management.service.IExamPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exam")
public class ExamPlanController {

    @Autowired
    private IExamPlanService examPlanService;

    @GetMapping("/list")
    public Result list(@RequestParam Long userId) {
        QueryWrapper<ExamPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByAsc("exam_date"); // 按考试时间升序
        List<ExamPlan> list = examPlanService.list(queryWrapper);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result add(@RequestBody ExamPlan examPlan) {
        examPlanService.save(examPlan);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody ExamPlan examPlan) {
        examPlanService.updateById(examPlan);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        examPlanService.removeById(id);
        return Result.success();
    }
}