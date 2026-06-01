package com.example.self_management.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.self_management.common.Result;
import com.example.self_management.entity.Finance;
import com.example.self_management.service.IFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/finance")
@CrossOrigin // 🌟 修复：去掉了写死的 localhost，允许公网跨域访问
public class FinanceController {

    @Autowired
    private IFinanceService financeService;

    // 🌟 核心魔法：专为 ECharts 大屏提供财务双塔对比数据！
    @GetMapping("/stats")
    public Result stats(@RequestParam Integer userId) {
        QueryWrapper<Finance> queryWrapper = new QueryWrapper<>();

        // MySQL 专属魔法：利用 DATE_FORMAT 截取年月，并按类型分组求和
        queryWrapper.select("DATE_FORMAT(record_date, '%Y-%m') as month", "type", "SUM(amount) as total")
                .eq("user_id", userId)
                .groupBy("DATE_FORMAT(record_date, '%Y-%m')", "type")
                .orderByAsc("month"); // 按月份顺时间线排序

        List<Map<String, Object>> list = financeService.listMaps(queryWrapper);
        return Result.success(list);
    }

    // 1. 新增账单
    @PostMapping("/add")
    public Result add(@RequestBody Finance finance) {
        financeService.save(finance);
        return Result.success("记账成功！");
    }

    // 2. 删除账单
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        financeService.removeById(id);
        return Result.success("删除成功！");
    }

    // 3. 修改账单
    @PutMapping("/update")
    public Result update(@RequestBody Finance finance) {
        financeService.updateById(finance);
        return Result.success("修改成功！");
    }

    // 4. 查询账单列表（只查当前用户的，按日期倒序排）
    @GetMapping("/list")
    public Result list(@RequestParam Integer userId) {
        QueryWrapper<Finance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("record_date");

        List<Finance> list = financeService.list(queryWrapper);
        return Result.success(list);
    }
}