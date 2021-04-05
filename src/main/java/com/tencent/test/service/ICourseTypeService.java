package com.tencent.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tencent.test.entity.CourseType;
import com.tencent.test.model.CourseModel;

import java.util.List;

public interface ICourseTypeService extends IService<CourseType> {
    //插入课程数据
    void insertData(List<CourseType> courseTypeList, List<CourseModel> courseList);
}
