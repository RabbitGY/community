package com.gy.community.dao;

import com.gy.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

//@Repository也可以用，但@Mapper是spring集成mybatis的，更简单
@Mapper
public interface UserMapper {
    User selectById(int id);

    User selectByName(String name);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
