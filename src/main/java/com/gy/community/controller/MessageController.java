package com.gy.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.gy.community.entity.Message;
import com.gy.community.entity.Page;
import com.gy.community.entity.User;
import com.gy.community.service.MessageService;
import com.gy.community.service.UserService;
import com.gy.community.utils.CommunityConstant;
import com.gy.community.utils.CommunityUtil;
import com.gy.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //私信列表
    @RequestMapping("/letter/list")
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        //处理分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        //私信列表
        List<Message> conversationList =  messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null){
            for (Message message:conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                //单个会话的会读消息
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        //总的未读消息
        model.addAttribute("letterUnreadCount", messageService.findLetterUnreadCount(user.getId(), null));
        //查询未读通知数量
        int noticeUnreadCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/letter";
    }

    @RequestMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page){
        //处理分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        //私信消息列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        //存储未读消息
        List<Integer> unreadIds = new ArrayList<>();
        if (letters != null ){
            for (Message message : letterList) {
                if (message.getStatus() == 0 && message.getToId() == hostHolder.getUser().getId()){
                    unreadIds.add(message.getId());
                }
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        //未读消息都改为已读
        if (!unreadIds.isEmpty()){
            messageService.readMessage(unreadIds);
        }

        model.addAttribute("letters", letters);
        model.addAttribute("target", getLetterTarget(conversationId));
        return "/site/letter-detail";
    }


    private User getLetterTarget(String conversationId){
        User user = hostHolder.getUser();

        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if(user.getId() == id0){
            return userService.findUserById(id1);
        }else{
            return userService.findUserById(id0);
        }
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content){
        User target = userService.findUserByName(toName);
        if (StringUtils.isBlank(content)){
            return CommunityUtil.getJSONString(1, "消息不能为空");
        }
        if (target == null){
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }
        Message message = new Message();
        int fromId = hostHolder.getUser().getId();
        int toId = target.getId();
        message.setFromId(fromId);
        message.setToId(toId);
        if (fromId < toId){
            message.setConversationId(fromId + "_" + toId);
        }else{
            message.setConversationId(toId + "_" + fromId);
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }


    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();
        //查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageVO = new HashMap<>();
        if (message != null){
            messageVO.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, Map.class);
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("count", count);
            int unread = messageService.findUnreadNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("unread", unread);
            messageVO.put("hasComment", 1);
        }else{
            messageVO.put("hasComment", 0);
        }
        model.addAttribute("commentNotice", messageVO);
        //查询点赞类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        messageVO = new HashMap<>();
        if (message != null){
            messageVO.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, Map.class);
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVO.put("count", count);
            int unread = messageService.findUnreadNoticeCount(user.getId(), TOPIC_LIKE);
            messageVO.put("unread", unread);
            messageVO.put("hasLike", 1);
        }else{
            messageVO.put("hasLike", 0);
        }
        model.addAttribute("likeNotice", messageVO);
        //查询关注类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        messageVO = new HashMap<>();
        if (message != null){
            messageVO.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, Map.class);
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("count", count);
            int unread = messageService.findUnreadNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("unread", unread);
            messageVO.put("hasFollow", 1);
        }else{
            messageVO.put("hasFollow", 0);
        }
        model.addAttribute("followNotice", messageVO);

        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        //查询未读通知数量
        int noticeUnreadCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }

    @RequestMapping(path ="/notice/detail/{topic}",method = RequestMethod.GET)
    public String getNoticeList(@PathVariable("topic") String topic, Model model, Page page){
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVOList = new ArrayList<>();
        List<Integer> unreadIds = new ArrayList<>();
        if (noticeList != null){
            for (Message notice : noticeList) {
                if (notice.getStatus() == 0 && notice.getToId() == hostHolder.getUser().getId()){
                    unreadIds.add(notice.getId());
                }
                Map<String, Object> map = new HashMap<>();
                map.put("notice", notice);
                //内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));

                //通知作者
                map.put("fromUser", userService.findUserById(notice.getFromId()));
                noticeVOList.add(map);
            }
        }
        model.addAttribute("notices", noticeVOList);

        //未读消息都改为已读
        if (!unreadIds.isEmpty()){
            messageService.readMessage(unreadIds);
        }
        return "/site/notice-detail";
    }


}
