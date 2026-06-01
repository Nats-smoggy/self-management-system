package com.example.self_management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.self_management.entity.Focus;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 🌟 就是缺了这个大宝贝！！！
public interface FocusMapper extends BaseMapper<Focus> {
}