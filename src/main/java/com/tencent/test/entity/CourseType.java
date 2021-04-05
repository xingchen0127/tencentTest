package com.tencent.test.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CourseType {

    private Integer id;

    private String typeName;

    private Integer courseNum;

    private String url;

    private LocalDateTime createTime;
}
