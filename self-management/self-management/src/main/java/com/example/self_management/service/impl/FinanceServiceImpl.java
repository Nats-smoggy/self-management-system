package com.example.self_management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.self_management.entity.Finance;
import com.example.self_management.mapper.FinanceMapper;
import com.example.self_management.service.IFinanceService;
import org.springframework.stereotype.Service;

@Service
public class FinanceServiceImpl extends ServiceImpl<FinanceMapper, Finance> implements IFinanceService {
}