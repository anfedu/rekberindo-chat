package com.amr.chatservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummary {
    private String id;
    private String phoneNumber;
    private String email;
    private String noKtp;
    private String address;
    private String name;
    private String profileImg;
    private String role;
    private ChatMessage lastChat;

}
