package com.qxq.dao;

import com.qxq.po.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    //通过用户名和密码查询用户
    User findByUsernameAndPassword(String username,String password);
}
