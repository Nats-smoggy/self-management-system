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
@CrossOrigin
public class FinanceController {

    @Autowired
    private IFinanceService financeService;

    @GetMapping("/stats")
    public Result stats(@RequestParam Integer userId) {
        QueryWrapper<Finance> queryWrapper = new QueryWrapper<>();

        // PostgreSQL uses to_char on parsed date text for year-month grouping.
        queryWrapper.select(
                        "to_char(to_date(record_date, 'YYYY-MM-DD'), 'YYYY-MM') as month",
                        "type",
                        "SUM(amount) as total"
                )
                .eq("user_id", userId)
                .groupBy("to_char(to_date(record_date, 'YYYY-MM-DD'), 'YYYY-MM')", "type")
                .orderByAsc("month");

        List<Map<String, Object>> list = financeService.listMaps(queryWrapper);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Finance finance) {
        financeService.save(finance);
        return Result.success("记账成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        financeService.removeById(id);
        return Result.success("删除成功");
    }

    @PutMapping("/update")
    public Result update(@RequestBody Finance finance) {
        financeService.updateById(finance);
        return Result.success("修改成功");
    }

    @GetMapping("/list")
    public Result list(@RequestParam Integer userId) {
        QueryWrapper<Finance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("record_date");

        List<Finance> list = financeService.list(queryWrapper);
        return Result.success(list);
    }
}
