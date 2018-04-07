package com.pengjinfei.incubate.service;

import com.pengjinfei.incubate.mapper.UserMapper;
import com.pengjinfei.incubate.model.User;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created on 4/2/18
 *
 * @author Pengjinfei
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private SqlSessionTemplate batchSqlTemplate;

    @Autowired
    private UserMapper userMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public void batchInsert(List<User> users) {
        UserMapper mapper = batchSqlTemplate.getMapper(UserMapper.class);
        for (User user : users) {
            mapper.insertOne(user);
        }
    }

    public void normalInsert(List<User> users) {
        for (User user : users) {
            userMapper.insertOne(user);
        }
    }


}
