package com.gy.community.service;

import com.gy.community.dao.DiscussPostMapper;
import com.gy.community.dao.UserMapper;
import com.gy.community.entity.DiscussPost;
import com.gy.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Service
public class AlphaService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    // propagation事务传播机制
    // a调b，a则是b的外部事务
    // REQUIRED：支持当前事务（外部事务），如果不存在，就创建新事物
    // REQUIRES_NEW：创建一个新的事务，并且暂停当前事务（外部事务）
    // NESTED：如果当前存在事务（外部事务），则嵌套在该事务中执行（但有独立的提交和回滚）；否则和REQUIRED一样
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        //新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt("aaaa");
        user.setPassword("aaaaaaaaa");
        user.setEmail("aaa@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("打火机卡好看");
        post.setContent("gdgskfgsukdga丢撒肯定撒开");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");
        return "OK";
    }

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                //新增用户
                User user = new User();
                user.setUsername("alpha");
                user.setSalt("aaaa");
                user.setPassword("aaaaaaaaa");
                user.setEmail("aaa@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);
                //新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("打火机卡好看");
                post.setContent("gdgskfgsukdga丢撒肯定撒开");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");
                return "ok";
            }
        });
    }
}
