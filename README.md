### 1. **数据库设计**

#### 1.1 **总体设计**

使用mysql作为存储数据的数据库，共有course(课程表)、courseType(课程分别表)两张表。

 

#### **1.2概念模型设计**

![img](.\images\wps1.jpg) 

#### **1.3数据库表设计**

##### **1.3.1 课程表**

**CREATE** **TABLE** `course` (

 `id` **int**(11) **NOT** **NULL** **AUTO_INCREMENT** COMMENT '主键',

 `course_type` **int**(11) **NOT** **NULL** **DEFAULT** '-1' COMMENT '课程类型',

 `course_title` **varchar**(100) **DEFAULT** **NULL** COMMENT '课程标题',

 `price` **varchar**(100) **DEFAULT** **NULL** COMMENT '价格',

 `teacher` **varchar**(100) **DEFAULT** **NULL** COMMENT '老师',

 `create_time` **timestamp** **NULL** **DEFAULT** **CURRENT_TIMESTAMP** **ON** **UPDATE** **CURRENT_TIMESTAMP** COMMENT '创建时间',

 `url` **varchar**(100) **DEFAULT** **NULL** COMMENT 'url',

 **PRIMARY** **KEY** (`id`)

) ENGINE=InnoDB **AUTO_INCREMENT**=93016 **DEFAULT** CHARSET=utf8;

 

 

##### **1.3.2 课程分类表**

**CREATE** **TABLE** `course_type` (

 `id` **int**(11) **NOT** **NULL** **AUTO_INCREMENT** COMMENT '主键',

 `type_name` **varchar**(100) **NOT** **NULL** COMMENT '类型名称',

 `course_num` **int**(11) **NOT** **NULL** **DEFAULT** '0' COMMENT '课程数量',

 `create_time` **timestamp** **NULL** **DEFAULT** **CURRENT_TIMESTAMP** **ON** **UPDATE** **CURRENT_TIMESTAMP** COMMENT '创建时间',

 `url` **varchar**(100) **DEFAULT** **NULL**,

 **PRIMARY** **KEY** (`id`)

) ENGINE=InnoDB **AUTO_INCREMENT**=289 **DEFAULT** CHARSET=utf8;

 

 

### **2系统设计**

#### **2.1技术选型**

使用SpringBoot搭建项目基础架构， Thymeleaf作为前端页面模板，使用Mybatis-Plus操作数据库，Jsoup作为网络爬虫工具。

 

#### **2.2实现过程**

#### **2.2.1爬取课程数据**

**2.2.1.1使用定时任务定时启动爬虫任务，使用Jsoup首先分别获取一级、二级、三级分类的url，用于后续爬取课程数据。**

![img](.\images\wps2.jpg) 

 

获取html标签的通用方法

![img](.\images\wps3.jpg) 

 

获取一级课程分类url

![img](.\images\wps4.jpg) 

 

获取二级课程分类url

![img](.\images\wps5.jpg) 

 

 

获取三级分类课程，并保存到CourseType对象中

![img](.\images\wps6.jpg) 

 

**2.2.1.2使用线程池爬取每个三级分类下所有的课程信息**

![img](.\images\wps7.jpg) 

 

获取三级分类下所有的课程信息

![img](.\images\wps8.jpg) 

 

递归获取下一页数据

![img](.\images\wps9.jpg) 

 

**2.2.1.3 得到所有课程信息和课程分类信息并插入数据库**

![img](.\images\wps10.jpg) 

 

#### **2.2.2 展示课程分类列表**

主要使用Mybatis-Plus中自带的分页方法实现分页查询

![img](.\images\wps11.jpg) 

 

#### **2.2.3 展示课程列表**

主要使用Mybatis-Plus中自带的分页方法实现分页查询

![img](.\images\wps12.jpg) 

 