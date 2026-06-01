package com.example.self_management.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.self_management.entity.SysUser;
import com.example.self_management.mapper.SysUserMapper;
import com.example.self_management.service.SysUserService;
import org.springframework.stereotype.Service; // ⚠️ 确保引入了这个包

@Service // ⚠️ 就是少了这个注解！它是告诉 Spring Boot “请把这个类收编管理” 的身份证
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

}