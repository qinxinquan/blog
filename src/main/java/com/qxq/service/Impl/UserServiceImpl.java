package com.qxq.service.Impl;

import com.qxq.dao.UserRepository;
import com.qxq.po.User;
import com.qxq.service.UserService;
import com.qxq.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public User checkUser(String username, String password) {

        User user = userRepository.findByUsernameAndPassword(username, MD5Utils.code(password));
        return user;
    }
}
