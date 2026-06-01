package com.example.self_management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.self_management.mapper.WeightRecordMapper;
import com.example.self_management.entity.WeightRecord;
import com.example.self_management.service.IWeightRecordService;
import org.springframework.stereotype.Service;

@Service
public class WeightRecordServiceImpl extends ServiceImpl<WeightRecordMapper, WeightRecord> implements IWeightRecordService {
}