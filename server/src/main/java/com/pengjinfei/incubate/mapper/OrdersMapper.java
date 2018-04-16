package com.pengjinfei.incubate.mapper;

import com.pengjinfei.incubate.dto.OrdersDTO;
import com.pengjinfei.incubate.model.Orders;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author PENGJINFEI533
 * @since 2018-04-16
 */
public interface OrdersMapper extends Mapper<Orders> {

	OrdersDTO selectOrderAndUser(Integer id);
}
