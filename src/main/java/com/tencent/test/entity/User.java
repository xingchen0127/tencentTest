package com.tencent.test.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class User implements Serializable {

    private Integer id;

    private String userName;

    private Integer age;
}
