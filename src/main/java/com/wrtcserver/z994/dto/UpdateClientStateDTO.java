package com.wrtcserver.z994.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zexho
 * @date 2020/10/23 3:36 下午
 */
@Data
public class UpdateClientStateDTO implements Serializable {
    private String reqAccount;
    private String targetAccount;
    private String type;
    private Boolean value;

    public UpdateClientStateDTO() {
    }

    public UpdateClientStateDTO(String reqAccount, String targetAccount, String type, Boolean value) {
        this.reqAccount = reqAccount;
        this.targetAccount = targetAccount;
        this.type = type;
        this.value = value;
    }
}
