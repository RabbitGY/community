package com.gy.community.controller;

import com.gy.community.annotation.LoginRequired;
import com.gy.community.entity.User;
import com.gy.community.service.FollowService;
import com.gy.community.service.LikeService;
import com.gy.community.service.UserService;
import com.gy.community.utils.CommunityConstant;
import com.gy.community.utils.CommunityUtil;
import com.gy.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping(path="/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    //取用户
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path="/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage == null){
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf('.') + 1);
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error", "文件格式不正确");
            return "/site/setting";
        }

        //生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        //确定文件存放路径
        File dest = new File(uploadPath + "/" +fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器出现异常！", e);
        }
        //更新当前头像路径（web访问路径）
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放的路径
        fileName = uploadPath + "/" + fileName;
        //解析文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf('.') + 1);
        //响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1){
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path="/updatePassword", method = RequestMethod.POST)
    public String updatePawwword(String oldPassword, String newPassword, @CookieValue("ticket") String ticket, Model model){
        if (StringUtils.isBlank(oldPassword)){
            model.addAttribute("oldPasswordMsg", "原密码不能为空！");
            return "/site/setting";
        }
        if (StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordMsg", "新密码不能为空！");
            return "/site/setting";
        }
        if (newPassword.length() < 8 ){
            model.addAttribute("newPasswordMsg", "新密码长度不能少于8位！");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        int userId = user.getId();
        User userResult = userService.findUserById(userId);
        if (!userResult.getPassword().equals(CommunityUtil.md5(oldPassword + userResult.getSalt()))){
            model.addAttribute("oldPasswordMsg", "请输入正确的原密码！");
            return "/site/setting";
        }
        userService.updatePassword(userId, CommunityUtil.md5(newPassword + userResult.getSalt()));
        userService.logout(ticket);
        return "redirect:/login";
    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePath(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("该用户不存在！");
        }
        //用户
        model.addAttribute("user", user);
        //获赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        //关注人数
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_PERSON);
        model.addAttribute("followeeCount", followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_PERSON, userId);
        model.addAttribute("followerCount", followerCount);
        //是否已关注
        boolean hasFollow = false;
        if (hostHolder.getUser() != null){
            hasFollow = followService.hasFollow(hostHolder.getUser().getId(), ENTITY_TYPE_PERSON, userId);
        }
        model.addAttribute("hasFollow", hasFollow);

        return "/site/profile";
    }

}
