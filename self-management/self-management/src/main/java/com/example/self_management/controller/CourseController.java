package com.example.self_management.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.self_management.common.Result;
import com.example.self_management.entity.Course;
import com.example.self_management.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.excel.EasyExcel;

import java.util.List; // 🌟 赛博教官顺手帮你补齐了最基础的 List 导包

@RestController
@RequestMapping("/course")
@CrossOrigin // 🌟 允许前端跨域访问
public class CourseController {

    @Autowired
    private ICourseService courseService;

    // 1. 获取当前用户的课表
    @GetMapping("/list")
    public Result list(@RequestParam Integer userId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return Result.success(courseService.list(queryWrapper));
    }

    // 2. 手动添加课程
    @PostMapping("/add")
    public Result add(@RequestBody Course course) {
        courseService.save(course);
        return Result.success("课程添加成功！");
    }

    // 3. 修改课程
    @PutMapping("/update")
    public Result update(@RequestBody Course course) {
        courseService.updateById(course);
        return Result.success("课程修改成功！");
    }

    // 4. 删除单节课
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        courseService.removeById(id);
        return Result.success("删除成功！");
    }

    // 5. 一键清空课表 (前端有个大红按钮需要这个)
    @DeleteMapping("/clear/{userId}")
    public Result clear(@PathVariable Integer userId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        courseService.remove(queryWrapper);
        return Result.success("课表已全部清空！");
    }

    // 6. 真正的 EasyExcel 导入接口
    @PostMapping("/import")
    public Result importExcel(@RequestParam("file") MultipartFile file, @RequestParam Integer userId) {
        try {
            // 核心魔法：分两步走，完美避开 Java 转换优先级的报错！
            List<?> rawList = EasyExcel.read(file.getInputStream())
                    .head(Course.class)
                    .sheet()
                    .doReadSync();

            // 第二步：再把它安安全全地转成我们的 Course 列表
            @SuppressWarnings("unchecked")
            List<Course> list = (List<Course>) rawList;

            // 遍历并补充是哪个用户的课表，然后存入数据库
            for (Course course : list) {
                course.setUserId(userId);
                courseService.save(course);
            }
            return Result.success("太牛了！成功解析并导入 " + list.size() + " 节课程！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("解析失败！请确保你的 Excel 包含：课程名称,星期几,第几节,上课地点,任课教师");
        }
    }
}