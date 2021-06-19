package com.cxylk.server.impl;

import com.cxylk.server.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	public String getName(String id) {
		return "lk " + id;
	}
}
