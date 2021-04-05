package com.tencent.test.controller;

import com.tencent.test.entity.User;
import com.tencent.test.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    private static Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private IUserService userService;

    @RequestMapping("/listUser")
    public List<User> queryUserList(){
        logger.info("查询所有用户");
        return userService.list();
    }
}
