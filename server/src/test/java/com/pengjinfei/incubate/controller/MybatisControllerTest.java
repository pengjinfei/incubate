package com.pengjinfei.incubate.controller;

import com.pengjinfei.incubate.mapper.UserMapper;
import com.pengjinfei.incubate.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MybatisControllerTest {

    @Mock
    private UserMapper userMapper;

    private MybatisController controller = new MybatisController();

    @Before
    public void mock() {
        User user = new User();
        user.setAge(1);
        user.setName("mock");
        Mockito.when(userMapper.selectTags(1)).thenReturn(user);
        controller.setUserMapper(userMapper);
    }

    @Test
    public void addUser() {
        User user = controller.selectTags(1);
        Assert.assertEquals(user.getName(),"mock");
    }
}