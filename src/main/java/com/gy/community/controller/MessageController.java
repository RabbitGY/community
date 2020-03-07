package com.gy.community.controller;

import com.gy.community.entity.Message;
import com.gy.community.entity.Page;
import com.gy.community.entity.User;
import com.gy.community.service.MessageService;
import com.gy.community.service.UserService;
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

import java.util.*;

@Controller
public class MessageController {
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
        Integer.parseInt("asb");
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


}
