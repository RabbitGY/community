package com.gy.community.utils;

import com.gy.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用户代替session对象
 * 线程隔离
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUsers(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
