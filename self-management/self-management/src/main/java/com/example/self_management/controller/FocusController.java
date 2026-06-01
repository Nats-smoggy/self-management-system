package com.example.self_management.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.self_management.common.Result;
import com.example.self_management.entity.Focus;
import com.example.self_management.service.IFocusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/focus")
@CrossOrigin
public class FocusController {

    @Autowired
    private IFocusService focusService;

    // 1. 接收番茄钟前端传来的专注记录
    @PostMapping("/add")
    public Result add(@RequestBody Focus focus) {
        focusService.save(focus);
        return Result.success("专注记录保存成功！");
    }

    // 🌟 2. 大屏专供：按日期分组求和，返回用户每天的专注总分钟数
    @GetMapping("/stats")
    public Result stats(@RequestParam Integer userId) {
        QueryWrapper<Focus> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("focus_date as date", "SUM(duration) as total")
                .eq("user_id", userId)
                .groupBy("focus_date");
        List<Map<String, Object>> list = focusService.listMaps(queryWrapper);
        return Result.success(list);
    }
}