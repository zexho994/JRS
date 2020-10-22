package com.wrtcserver.z994.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zexho
 * @date 2020/10/21 10:39 上午
 */
@Data
public class SignalMsg implements Serializable
{
    private String roomId;

    private String account;

    public SignalMsg() {
    }

    public SignalMsg(String roomId, String account) {
        this.roomId = roomId;
        this.account = account;
    }
}