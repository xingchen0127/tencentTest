package com.tencent.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.test.dao.CourseTypeMapper;
import com.tencent.test.entity.Course;
import com.tencent.test.entity.CourseType;
import com.tencent.test.model.CourseModel;
import com.tencent.test.service.ICourseService;
import com.tencent.test.service.ICourseTypeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CourseTypeServiceImpl extends ServiceImpl<CourseTypeMapper, CourseType>
        implements ICourseTypeService {

    @Autowired
    private CourseTypeMapper courseTypeMapper;

    @Autowired
    private ICourseService courseService;

    @Override
    public void insertData(List<CourseType> courseTypeList, List<CourseModel> courseList) {
        Map<String, Integer> typeIdMap = new HashMap<>();
        if (courseTypeList != null || courseTypeList.size() > 0){

            this.saveBatch(courseTypeList);

            courseTypeList.stream().forEach(type -> {
                typeIdMap.put(type.getTypeName(), type.getId());
            });
        }

        List<Course> saveList = new LinkedList<>();

        courseList.stream().forEach(courseModel -> {
            Course course = new Course();
            BeanUtils.copyProperties(courseModel, course);
            course.setCourseType(typeIdMap.get(courseModel.getCourseTypeName()));

            saveList.add(course);
        });

        if (saveList != null && saveList.size() > 0){
            courseService.saveBatch(saveList, 5000);
        }

    }
}
