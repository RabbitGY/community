package com.gy.community;

import com.gy.community.dao.DiscussPostMapper;
import com.gy.community.dao.UserMapper;
import com.gy.community.entity.DiscussPost;
import com.gy.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser(){
        List<DiscussPost> res = discussPostMapper.selectDiscussPosts(0,0,10);
        for (DiscussPost dp: res) {
            System.out.println(dp);
        }

        int ress = discussPostMapper.selectDiscussPostRows(101);
        System.out.println(ress);
    }


}
