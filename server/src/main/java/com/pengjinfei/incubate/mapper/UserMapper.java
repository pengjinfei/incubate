package com.pengjinfei.incubate.mapper;


import com.github.pagehelper.Page;
import com.pengjinfei.incubate.model.User;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * Created on 4/1/18
 *
 * @author Pengjinfei
 */
public interface UserMapper extends Mapper<User> {

    User selectById(Integer id);

    Page<User> selectAutoPage(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

    void insertOne(User user);

}
