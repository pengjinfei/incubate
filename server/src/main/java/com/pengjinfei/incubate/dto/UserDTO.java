package com.pengjinfei.incubate.dto;

import com.pengjinfei.incubate.model.Orders;
import com.pengjinfei.incubate.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author PENGJINFEI533
 * @since 2018-04-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserDTO  extends User implements Serializable {

	public UserDTO(Integer id, Integer age) {
		super(id, age);
	}

	public UserDTO() {
		super();
	}

	private static final long serialVersionUID = 4503619151446018227L;
	private List<Orders> ordersList;
}
