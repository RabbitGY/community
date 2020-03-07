package com.gy.community.dao;

import com.gy.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated//用redis重构l
public interface LoginTicketMapper {
    //用注解写sql优点少一个文件，缺点是不方便阅读，写标签不方便
    @Insert({
            "insert into login_ticket (user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")//自动生成主键
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket ",
            "where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //动态sql示例
    @Update({
            "<script>",
            "update login_ticket set status = #{status} ",
            "where ticket = #{ticket} ",
            "<if test=\"ticket != null\">",
            "and 1=1",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);
}
