package com.amr.chatservice.configuration;

import com.amr.chatservice.redis.SessionWS;
import com.amr.chatservice.service.HtppClients;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    SessionWS sessionWSRedis;

    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) {
        System.out.println(event);
        String userId=new JSONObject(event).getJSONObject("message").getJSONObject("headers").getJSONObject("nativeHeaders").getJSONArray("userId").get(0).toString();
        String sessionId = SimpAttributesContextHolder.currentAttributes().getSessionId();
        logger.debug(userId+" is Online session id "+sessionId);
        sessionWSRedis.create(sessionId,userId);
        messagingTemplate.convertAndSend(
                "/users", new JSONObject().put("userId",userId).put("status","ONLINE").toString());
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = SimpAttributesContextHolder.currentAttributes().getSessionId();
        String userId=sessionWSRedis.fetchOneById(sessionId);
        logger.debug(userId+" is Offline session id "+sessionId);
        sessionWSRedis.delete(sessionId);
        messagingTemplate.convertAndSend(
                "/users",new JSONObject().put("userId",userId).put("status","OFFLINE"));
    }
}