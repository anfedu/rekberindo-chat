package com.amr.chatservice.redis;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class SessionWS {
    @Value("${redis.chatservice.key.sessionws}")
    private String KEY;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String,String> hashOps;
    public void create(String id,String userId) {
        hashOps.putIfAbsent(KEY + id, id, userId);
    }
    public String fetchOneById(String id) {
        String obj = hashOps.get(KEY + id, id);
        return obj;
    }

    public void delete(String id) {
        hashOps.delete(KEY + id,id);
    }
}
