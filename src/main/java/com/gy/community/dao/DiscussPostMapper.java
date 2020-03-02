package com.gy.community.dao;

import com.gy.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //@Param("userId")给参数取别名
    //若只有这一个参数，而且用到<if>动态条件，必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);
}
