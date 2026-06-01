package com.example.self_management.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.self_management.common.Result;
import com.example.self_management.entity.WeightRecord;
import com.example.self_management.service.IWeightRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weight")
public class WeightRecordController {

    @Autowired
    private IWeightRecordService weightRecordService;

    @GetMapping("/list")
    public Result list(@RequestParam Long userId) {
        QueryWrapper<WeightRecord> queryWrapper = new QueryWrapper<>();
        // 获取最新的7天记录，按照日期倒序拿，然后通过前端或工具类倒序排列
        queryWrapper.eq("user_id", userId).orderByDesc("record_date").last("LIMIT 7");
        List<WeightRecord> list = weightRecordService.list(queryWrapper);
        java.util.Collections.reverse(list); // 反转使其在图表中按日期从旧到新展示
        return Result.success(list);
    }

    @PostMapping("/upload")
    public Result upload(@RequestBody WeightRecord weightRecord) {
        QueryWrapper<WeightRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", weightRecord.getUserId())
                .eq("record_date", weightRecord.getRecordDate());
        WeightRecord exist = weightRecordService.getOne(queryWrapper);

        if (exist != null) {
            exist.setWeight(weightRecord.getWeight());
            weightRecordService.updateById(exist); // 存在则更新当天数据
        } else {
            weightRecordService.save(weightRecord); // 不存在则新增
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        weightRecordService.removeById(id);
        return Result.success();
    }
}