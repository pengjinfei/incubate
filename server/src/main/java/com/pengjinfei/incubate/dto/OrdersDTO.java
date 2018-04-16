package com.pengjinfei.incubate.dto;

import com.pengjinfei.incubate.model.Orders;
import com.pengjinfei.incubate.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author PENGJINFEI533
 * @since 2018-04-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrdersDTO extends Orders implements Serializable {
	private static final long serialVersionUID = 6154493071142203338L;
	private User user;
}
