package com.open.ai.eros.ai.manager;

import com.open.ai.eros.ai.bean.req.AddChatMessageReq;
import com.open.ai.eros.ai.bean.req.ConversionChatMessageReq;
import com.open.ai.eros.ai.bean.req.UpdateChatMessageReq;
import com.open.ai.eros.ai.bean.vo.ChatMessageVo;
import com.open.ai.eros.ai.bean.vo.ConversionChatMessageVo;
import com.open.ai.eros.ai.bean.vo.SenderVo;
import com.open.ai.eros.ai.convert.ChatConvert;
import com.open.ai.eros.ai.processor.ChatMessageSaveProcessor;
import com.open.ai.eros.ai.processor.message.bean.ChatMessageSaveParam;
import com.open.ai.eros.common.config.CustomIdGenerator;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.bean.vo.BMaskVo;
import com.open.ai.eros.creator.manager.MaskManager;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.ai.entity.ChatMessage;
import com.open.ai.eros.db.mysql.ai.entity.GetChatMessageVo;
import com.open.ai.eros.db.mysql.ai.service.impl.ChatMessageServiceImpl;
import com.open.ai.eros.db.mysql.ai.service.impl.ShareMaskServiceImpl;
import com.open.ai.eros.db.mysql.user.entity.User;
import com.open.ai.eros.db.mysql.user.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @类名：ChatMessageManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/4 14:24
 */

/**
 * 聊天消息管理
 */
@Component
@Slf4j
public class ChatMessageManager {


    @Autowired
    private ChatMessageServiceImpl chatMessageService;

    @Autowired
    private MaskManager maskManager;

    @Autowired
    private List<ChatMessageSaveProcessor> chatMessageSaveProcessors;

    @Autowired
    private CustomIdGenerator customIdGenerator;

    @Autowired
    private ShareMaskServiceImpl shareMaskService;

    @Autowired
    private UserServiceImpl userService;


    /**
     * 获取会话里面的对话信息
     * 安装时间倒序获取
     *
     * @param userId
     * @param req
     * @return
     */
    public ResultVO<ConversionChatMessageVo> getConversionChatMessage(Long userId, ConversionChatMessageReq req) {

        String conversionId = req.getConversionId();
        Integer pageSize = req.getPageSize();
        Integer pageNum = req.getPageNum();

        GetChatMessageVo.GetChatMessageVoBuilder voBuilder = GetChatMessageVo.builder()
                .conversionId(conversionId)
                .userId(userId)
                .pageIndex((pageNum - 1) * pageSize)
                .pageSize(pageSize);

        ConversionChatMessageVo.ConversionChatMessageVoBuilder builder = ConversionChatMessageVo.builder();
        builder.lastPage(true);

        List<ChatMessage> chatMessages = chatMessageService.getChatMessage(voBuilder.build());

        if (chatMessages.size() >= req.getPageSize()) {
            builder.lastPage(false);
        }
        List<ChatMessageVo> chatMessageVos = new ArrayList<>(chatMessages.size());
        for (int i = chatMessages.size() - 1; i >= 0; i--) {
            ChatMessage chatMessage = chatMessages.get(i);
            ChatMessageVo chatMessageVo = ChatConvert.I.convertChatMessageVo(chatMessage);
            String role = chatMessage.getRole();

            SenderVo.SenderVoBuilder senderVoBuilder = SenderVo.builder();

            if(role.equals(AIRoleEnum.ASSISTANT.getRoleName()) && chatMessage.getMaskId()!=null ){
                // ai角色
                BMaskVo maskVo = maskManager.getCacheBMaskById(chatMessage.getMaskId());
                if(maskVo!=null){
                    senderVoBuilder.userName(maskVo.getName());
                    senderVoBuilder.id(maskVo.getId());
                    senderVoBuilder.role(AIRoleEnum.ASSISTANT.getRoleName());
                    senderVoBuilder.avatar(maskVo.getAvatar());
                }
            }else{
                // 用户
                Optional<User> cacheUser = userService.getCacheUser(chatMessage.getUserId());
                if(cacheUser.isPresent()){
                    User user = cacheUser.get();
                    senderVoBuilder.userName(user.getUserName());
                    senderVoBuilder.id(user.getId());
                    senderVoBuilder.role(AIRoleEnum.USER.getRoleName());
                    senderVoBuilder.avatar(user.getAvatar());
                }
            }
            chatMessageVo.setSenderVo(senderVoBuilder.build());
            chatMessageVos.add(chatMessageVo);
        }
        builder.chatMessageVos(chatMessageVos);
        return ResultVO.success(builder.build());
    }



    /**
     * 删除用户的单个对话消息
     *
     * @param id
     * @return
     */
    public ResultVO backMessage(Long userId, Long id) {
        ChatMessage chatMessage = chatMessageService.getById(id);
        if(chatMessage==null || !userId.equals(chatMessage.getUserId())){
            return ResultVO.fail("没有权限回溯消息");
        }
        int result = chatMessageService.backMessage(chatMessage.getConversationId(),id);
        log.info("backMessage id={},account={} result={} ", id, userId, result);
        return result > 0 ? ResultVO.success() : ResultVO.fail("删除失败！");
    }



    /**
     * 删除用户的单个对话消息
     *
     * @param id
     * @return
     */
    public ResultVO<Set<Long>> deleteMessage(Long userId, Long id) {

        Set<Long> ids = new HashSet<>();
        ids.add(id);
        ChatMessage chatMessage = chatMessageService.getById(id);
        if(chatMessage==null || !Objects.equals(userId, chatMessage.getUserId())){
            return ResultVO.fail("消息不存在");
        }
        if(chatMessage.getRole().equals(AIRoleEnum.USER.getRoleName())){
            List<ChatMessage> chatMessages = chatMessageService.getByParentId(id);
            List<Long> parentIds = chatMessages.stream().map(ChatMessage::getId).collect(Collectors.toList());
            ids.addAll(parentIds);
        }
        boolean removeByIds = chatMessageService.removeByIds(ids);
        log.info("deleteMessage id={},account={} removeByIds={} ", id, userId, removeByIds);
        return removeByIds ? ResultVO.success(ids) : ResultVO.fail("删除失败！");
    }


    /**
     * 修改用户的单个对话消息
     *
     * @return
     */
    public ResultVO updateMessage(Long userId, UpdateChatMessageReq req) {

        ResultVO resultVO = maskManager.banWordInPrompt(req.getMaskId(), req.getContent());
        if (!resultVO.isOk()) {
            return resultVO;
        }

        int result = chatMessageService.updateById(req.getId(), userId, req.getContent());
        log.info("updateMessage id={},account={} result={} ", req.getContent(), userId, result);
        return result > 0 ? ResultVO.success() : ResultVO.fail("修改失败！");
    }


    /**
     * 保存用户的ai文字消息
     *
     * @param req
     * @return
     */
    public ResultVO<Map<String, String>> addMessage(CacheUserInfoVo cacheUserInfo, AddChatMessageReq req, HttpServletResponse response) {

        Long userId = cacheUserInfo.getId();
        String templateModel = req.getTemplateModel();
        String[] split = templateModel.split(":");
        if (split.length != 2) {
            return ResultVO.fail("传入的模型标识有问题");
        }
        ResultVO resultVO = maskManager.banWordInPrompt(req.getMaskId(), req.getContent());
        if (!resultVO.isOk()) {
            return ResultVO.fail(resultVO.getMsg());
        }
        ChatMessage entity = new ChatMessage();
        // 检测当前对话 最新的message不是 user，如果是user 则进行 更新操作
        ChatMessage newMessage = chatMessageService.getNewMessage(req.getConversationId(), userId);
        if (newMessage != null && newMessage.getRole().equals(AIRoleEnum.USER.getRoleName())) {
            entity.setId(newMessage.getId());
            entity.setContent(req.getContent());
            boolean updatedResult = chatMessageService.updateById(entity);
            log.info("addMessage userId={},updatedResult={}", userId, updatedResult);
            if (!updatedResult) {
                return ResultVO.fail("更新已有的消息失败！ ");
            }
        } else {
            long id = customIdGenerator.nextId(ChatMessage.class);
            long parentId = customIdGenerator.nextId(ChatMessage.class);
            entity.setId(id);
            entity.setRole(AIRoleEnum.USER.getRoleName());
            entity.setUserId(userId);
            entity.setModel(split[1]);
            entity.setParentId(parentId);
            entity.setMaskId(req.getMaskId());
            entity.setCreateTime(LocalDateTime.now());
            entity.setConversationId(req.getConversationId());
            entity.setContent(req.getContent());
            boolean saveResult = chatMessageService.save(entity);
            log.info("addMessage userId={},saveResult={}", userId, saveResult);
            if (!saveResult) {
                return ResultVO.fail("插入失败！");
            }
        }

        ChatMessageSaveParam param = ChatMessageSaveParam.builder()
                .chatMessage(entity)
                .cacheUserInfo(cacheUserInfo)
                .req(req)
                .build();
        for (ChatMessageSaveProcessor chatMessageSaveProcessor : chatMessageSaveProcessors) {
            ResultVO result = chatMessageSaveProcessor.after(param, response);
            if (!result.isOk()) {
                return ResultVO.fail(result.getMsg());
            }
        }

        Map<String, String> chatIdMap = new HashMap<>();
        chatIdMap.put("chatId", String.valueOf(entity.getId()));
        return ResultVO.success(chatIdMap);
    }


}
