package com.tencent.test.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tencent.test.entity.Course;
import com.tencent.test.service.ICourseService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/course")
public class CourseController {

    private static Logger logger = LoggerFactory.getLogger(CourseTypeController.class);

    @Autowired
    private ICourseService courseService;

    @RequestMapping("/listCourse")
    public String listCourse(Model model, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "typeId") int typeId){

        try {
            logger.info("查询课程参数： pageNum = " + pageNum + ", pageSize = "  + pageSize + ", typeId = " + typeId);

            Page<Course> coursePage = new Page<>();

            coursePage.setCurrent(pageNum);
            coursePage.setSize(pageSize);
            coursePage = courseService.page(coursePage, Wrappers.<Course>lambdaQuery()
                    .eq(Course::getCourseType, typeId)
            );

            model.addAttribute("pageData", coursePage);
            model.addAttribute("typeId", typeId);

            return "course";
        }catch (Exception e){
            logger.error("查询课程详情失败：");
            e.printStackTrace();

            return "error/500";
        }

    }
}
