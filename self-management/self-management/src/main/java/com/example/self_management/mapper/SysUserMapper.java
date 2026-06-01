package com.example.self_management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.self_management.entity.SysUser; // 注意：这里的路径要对应你的 SysUser 所在的包
import org.apache.ibatis.annotations.Mapper;

@Mapper // 告诉 Spring Boot 这是一个专门连数据库的 Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    // 继承了 BaseMapper，我们就自动拥有了增删改查的超能力，目前一行代码都不用写！
}