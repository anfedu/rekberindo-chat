package com.amr.chatservice.service;

import com.amr.chatservice.exception.ResourceNotFoundException;
import com.amr.chatservice.model.ChatMessage;
import com.amr.chatservice.model.MessageStatus;
import com.amr.chatservice.model.UserSummary;
import com.amr.chatservice.repository.ChatMessageRepository;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageService {
    @Autowired private ChatMessageRepository repository;
    @Autowired private ChatRoomService chatRoomService;
    @Autowired private MongoOperations mongoOperations;
    @Autowired private HtppClients htppClients;
    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        repository.save(chatMessage);
        return chatMessage;
    }

    public long countNewMessages(String senderId, String recipientId) {
        return repository.countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.RECEIVED);
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatId(senderId, recipientId, false);

        var messages =
                chatId.map(cId -> repository.findByChatId(cId)).orElse(new ArrayList<>());

        if(messages.size() > 0) {
            updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
        }

        return messages;
    }

    public ChatMessage findById(String id) {
        return repository
                .findById(id)
                .map(chatMessage -> {
                    chatMessage.setStatus(MessageStatus.DELIVERED);
                    return repository.save(chatMessage);
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException("can't find message (" + id + ")"));
    }

    public void updateStatuses(String senderId, String recipientId, MessageStatus status) {
        Query query = new Query(
                Criteria
                        .where("senderId").is(senderId)
                        .and("recipientId").is(recipientId));
        Update update = Update.update("status", status);
        mongoOperations.updateMulti(query, update, ChatMessage.class);
    }
    public List<UserSummary> contact(String senderId,String token) {
        var chatRoomList = chatRoomService.getChatRoomByChat(senderId);
        ArrayList<UserSummary> userSummaries = new ArrayList<UserSummary>();
        chatRoomList.forEach(chatRoom->{
            UserSummary user= null;
            try {
                user = findUserByRecipientId(chatRoom.getRecipientId(),token);
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<ChatMessage> chatMessages=findChatMessages(senderId,chatRoom.getRecipientId());
            if(chatMessages.size() > 0){
                user.setLastChat(chatMessages.get(chatMessages.size()-1));
            }else{
                user.setLastChat(new ChatMessage());
            }
            userSummaries.add(user);
        });
        System.out.println(chatRoomList);

        return userSummaries;
    }

    private UserSummary findUserByRecipientId(String token,String id) throws IOException {
        return htppClients.postUsers(token,id);
    }
}
