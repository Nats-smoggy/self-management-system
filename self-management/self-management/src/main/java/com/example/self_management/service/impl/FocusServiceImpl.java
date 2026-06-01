package com.example.self_management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.self_management.entity.Focus;
import com.example.self_management.mapper.FocusMapper;
import com.example.self_management.service.IFocusService;
import org.springframework.stereotype.Service;

@Service // 🌟 罪魁祸首就是漏了这行！！！没有它 Spring Boot 就不启动！
public class FocusServiceImpl extends ServiceImpl<FocusMapper, Focus> implements IFocusService {
}