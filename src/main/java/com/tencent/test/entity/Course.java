package com.tencent.test.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class Course {

    private Integer id;

    private String courseTitle;

    private Integer courseType;

    private String price;

    private String teacher;

    private LocalDateTime createTime;

    private String url;
}
