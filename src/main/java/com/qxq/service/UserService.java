package com.qxq.service;

import com.qxq.po.User;

public interface UserService {
    User checkUser(String username,String password);
}
