package com.pengjinfei.incubate.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pengjinfei.incubate.dto.OrdersDTO;
import com.pengjinfei.incubate.dto.UserDTO;
import com.pengjinfei.incubate.mapper.OrdersMapper;
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
    private OrdersMapper ordersMapper;

    @Autowired
    private UserService userService;

    @GetMapping("/users/{id}")
    public UserDTO selectOrdersByUserId(@PathVariable("id") Integer  id) {
        log.info("test sleuth");
        return userMapper.selectOrdersByUserId(id);
    }

    @GetMapping("/tags/{id}")
    public User selectTags(@PathVariable("id") Integer  id) {
        return userMapper.selectTags(id);
    }

	@GetMapping("/auto/user/{id}")
	public User selectByPrimaryKey(@PathVariable("id") Integer  id) {
		return userMapper.selectByPrimaryKey(id);
	}

	@PostMapping("/auto/user")
	public User insertSelective(@RequestBody User user) {
		userMapper.insertSelective(user);
		return user;
	}

    @PostMapping("/tags")
    public User addUserTags(@RequestBody User user) {
    	userMapper.insertTags(user);
		return user;
    }

    @GetMapping("/orders/{id}")
    public OrdersDTO selectOrderAndUser(@PathVariable("id") Integer  id) {
        return ordersMapper.selectOrderAndUser(id);
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

    @PostMapping("/batch")
    public void batchInsert() {
        userService.batchInsert(getMultiUser(10000));
    }

    @PostMapping("/normalbatch")
    public void normalInsert() {
        userService.normalInsert(getMultiUser(1000));
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
