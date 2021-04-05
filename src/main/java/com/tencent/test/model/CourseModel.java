package com.tencent.test.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CourseModel {

    private String courseTitle;

    private String courseTypeName;

    private String price;

    private String teacher;

    private LocalDateTime createTime;

    private String url;

}
