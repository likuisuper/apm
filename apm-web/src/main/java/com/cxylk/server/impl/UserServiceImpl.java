package com.cxylk.server.impl;

import com.alibaba.fastjson.JSON;
import com.cxylk.bean.User;
import com.cxylk.server.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	@Override
	public String getName(String id) {
		return JSON.toJSONString(new User("LK", 1));
	}
}
