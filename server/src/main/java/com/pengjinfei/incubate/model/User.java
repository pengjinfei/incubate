package com.pengjinfei.incubate.model;

import com.pengjinfei.incubate.configuration.mybatis.ListStringTypeHandler;
import lombok.Data;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

/**
 * Created on 4/1/18
 *
 * @author Pengjinfei
 */
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer age;
    @ColumnType(typeHandler = ListStringTypeHandler.class)
    private List<String> tags;
}
