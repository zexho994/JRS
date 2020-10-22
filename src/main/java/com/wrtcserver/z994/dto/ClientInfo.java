package com.wrtcserver.z994.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author Zexho
 * @date 2020/10/21 10:03 上午
 */
@Data
public class ClientInfo implements Serializable {
    private String account;

    private UUID sessionId;

    private String roomId;

    private Boolean audioMute = false;

    private Boolean videoMute = false;

    private Boolean screenMute = false;

    public ClientInfo(String account, UUID sessionId) {
        this.account = account;
        this.sessionId = sessionId;
    }

    public ClientInfo(String account, UUID sessionId, String roomid, boolean audioMute, boolean videoMute, boolean screenMute) {
        this.account = account;
        this.sessionId = sessionId;
        this.roomId = roomid;
        this.audioMute = audioMute;
        this.videoMute = videoMute;
        this.screenMute = screenMute;
    }

    public ClientInfo() {
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "account='" + account + '\'' +
                ", sessionId=" + sessionId +
                ", roomid='" + roomId + '\'' +
                ", audioMute=" + audioMute +
                ", videoMute=" + videoMute +
                ", screenMute=" + screenMute +
                '}';
    }
}