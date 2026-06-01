package com.example.self_management.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.self_management.common.Result;
import com.example.self_management.entity.SysUser;
import com.example.self_management.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // 注意：教官在这里帮你补上了 Map 的导包

@RestController
@RequestMapping("/user")
@CrossOrigin // 解决跨域问题
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 1. 登录接口（含账号存在校验与封禁拦截）
     */
    @PostMapping("/login")
    public Result login(@RequestBody SysUser sysUser) {
        // 先按用户名查询
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", sysUser.getUsername());
        SysUser dbUser = sysUserService.getOne(queryWrapper);

        // A. 校验账号是否存在
        if (dbUser == null) {
            return Result.error("登录失败：该账号不存在，请先注册！");
        }

        // B. 校验密码是否正确
        if (!dbUser.getPassword().equals(sysUser.getPassword())) {
            return Result.error("登录失败：密码错误，请检查！");
        }

        // C. 校验账号是否被封禁（逻辑封禁拦截）
        if (dbUser.getStatus() != null && dbUser.getStatus() == 0) {
            return Result.error("登录拒绝：您的账号已被管理员封禁，请联系站长！");
        }

        // 验证通过，返回用户信息
        return Result.success(dbUser);
    }

    /**
     * 2. 注册接口（含用户名格式检查、查重、密码复杂度校验）
     */
    @PostMapping("/register")
    public Result register(@RequestBody SysUser sysUser) {
        String username = sysUser.getUsername();
        String password = sysUser.getPassword();

        // --- A. 账号格式校验（只能是字母或数字，不能有特殊字符） ---
        // ^[a-zA-Z0-9]+$ 表示从头到尾只能由字母和数字组成
        if (username == null || !username.matches("^[a-zA-Z0-9]+$")) {
            return Result.error("注册失败：账号只能包含字母和数字，不能有特殊字符！");
        }

        // --- B. 密码复杂度校验（必须包含字母和数字的组合） ---
        // ^(?=.*[0-9])(?=.*[a-zA-Z]).+$ 表示必须同时包含数字和字母
        if (password == null || !password.matches("^(?=.*[0-9])(?=.*[a-zA-Z]).+$")) {
            return Result.error("注册失败：密码必须是字母和数字的组合！");
        }

        // --- C. 用户名查重过滤器 ---
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        SysUser existingUser = sysUserService.getOne(queryWrapper);

        if (existingUser != null) {
            return Result.error("注册失败：该账号已被占用，请尝试其他名称！");
        }

        // --- D. 校验通过，初始化并保存 ---
        sysUser.setRole("USER");
        sysUser.setStatus(1);

        boolean saved = sysUserService.save(sysUser);
        return saved ? Result.success("🎉 注册成功！") : Result.error("注册失败");
    }

    /**
     * 3. 获取所有用户列表（管理员控制台使用）
     */
    @GetMapping("/list")
    public Result findAll() {
        List<SysUser> list = sysUserService.list();
        return Result.success(list);
    }

    /**
     * 4. 提权接口：提拔普通用户为 ADMIN
     */
    @PutMapping("/promote/{id}")
    public Result promote(@PathVariable Integer id) {
        SysUser user = sysUserService.getById(id);
        if (user != null) {
            user.setRole("ADMIN");
            sysUserService.updateById(user);
            return Result.success("提拔成功，对方已获得管理员权限！");
        }
        return Result.error("操作失败：未找到该用户");
    }

    /**
     * 5. 状态管理接口：逻辑封禁/解封
     */
    @PutMapping("/status/{id}/{status}")
    public Result updateStatus(@PathVariable Integer id, @PathVariable Integer status) {
        SysUser user = sysUserService.getById(id);
        if (user != null) {
            user.setStatus(status); // 0 为封禁，1 为正常
            sysUserService.updateById(user);
            String msg = (status == 0) ? "账号已被移入封禁名单" : "账号已恢复正常使用";
            return Result.success(msg);
        }
        return Result.error("操作失败：用户不存在");
    }

    /**
     * 6. 更新用户信息（个人中心修改资料）
     */
    @PutMapping("/update")
    public Result update(@RequestBody SysUser sysUser) {
        boolean updated = sysUserService.updateById(sysUser);
        if (updated) {
            return Result.success("资料更新成功！");
        }
        return Result.error("更新失败");
    }

    /**
     * 7. 专属修改密码接口 (新增)
     */
    @PutMapping("/password")
    public Result updatePassword(@RequestBody Map<String, Object> params) {
        // 1. 从前端传来的 JSON 中提取参数
        Integer id = Integer.valueOf(params.get("id").toString());
        String oldPassword = (String) params.get("oldPassword");
        String newPassword = (String) params.get("newPassword");

        // 2. 去数据库查这个用户
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.error("操作失败：用户不存在！");
        }

        // 3. 核心校验：比对原密码对不对
        if (!user.getPassword().equals(oldPassword)) {
            return Result.error("修改失败：原密码错误！");
        }

        // 4. 原密码正确，换上新密码并保存
        user.setPassword(newPassword);
        sysUserService.updateById(user);

        return Result.success("密码修改成功！");
    }
}