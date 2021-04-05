package com.tencent.test.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tencent.test.entity.CourseType;
import com.tencent.test.service.ICourseTypeService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Wrapper;

@Slf4j
@Controller
@RequestMapping("/courseType")
public class CourseTypeController {

    private static Logger logger = LoggerFactory.getLogger(CourseTypeController.class);

    @Autowired
    private ICourseTypeService courseTypeService;

    @RequestMapping("/listType")
    public String listType(Model model, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        try{
            Page<CourseType> courseTypePage = new Page<>();

            logger.info("查询课程分类参数： pageNum = " + pageNum + ", pageSize = "  + pageSize);

            courseTypePage.setCurrent(pageNum);
            courseTypePage.setSize(pageSize);

            courseTypePage = courseTypeService.page(courseTypePage, Wrappers.<CourseType>lambdaQuery()
                .orderByDesc(CourseType::getCreateTime)
            );

            model.addAttribute("pageData", courseTypePage);

            return "courseType";
        }catch (Exception e){
            logger.error("查询课程分类失败：");
            e.printStackTrace();

            return "error/500";
        }

    }
}
