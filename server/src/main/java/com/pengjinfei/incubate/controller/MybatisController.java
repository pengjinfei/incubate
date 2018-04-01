package com.pengjinfei.incubate.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pengjinfei.incubate.mapper.UserMapper;
import com.pengjinfei.incubate.model.User;
import com.pengjinfei.incubate.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 4/1/18
 *
 * @author Pengjinfei
 */
@RestController
@Slf4j
public class MybatisController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @GetMapping("/users/{id}")
    public User test(@PathVariable("id") Integer  id) {
        log.info("test sleuth");
        return userMapper.selectById(id);
    }

    @GetMapping("/users/page")
    public PageInfo<User> getPage(@RequestParam(name = "pageNum",defaultValue = "1") int pageNum,
                                  @RequestParam(name = "pageSize",defaultValue = "3") int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(User.class);
        example.orderBy("age").desc();
        return new PageInfo<>(userMapper.selectByExample(example));
    }

    @GetMapping("/users/autoPage")
    public PageInfo<User> autoPage(@RequestParam(name = "pageNum",defaultValue = "1") int pageNum,
                                  @RequestParam(name = "pageSize",defaultValue = "3") int pageSize){
        return new PageInfo<>(userMapper.selectAutoPage(pageNum,pageSize));
    }

    @PostMapping("/user")
    public int addUser(@RequestBody User user) {
        userMapper.insertSelective(user);
        return user.getId();
    }

    @PostMapping("/normalbatch")
    public void batchInsert() {
        userService.normalInsert(getMultiUser(1000));
    }

    @PostMapping("/batch")
    public void normalInsert() {
        userService.batchInsert(getMultiUser(100));
    }

    private List<User> getMultiUser(int num) {
        List<User> users = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            User user = new User();
            user.setName(RandomStringUtils.randomAlphabetic(5));
            user.setAge(RandomUtils.nextInt(10,100));
            users.add(user);
        }
        return users;
    }
}
