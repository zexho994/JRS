package com.wrtcserver.z994;

import com.corundumstudio.socketio.SocketIOClient;
import com.wrtcserver.z994.dto.ClientInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据存储
 *
 * @author Zexho
 * @date 2020/10/29 4:31 下午
 */
public class Store {

    private static final Map<SocketIOClient, ClientInfo> CLIENT_STORE = new ConcurrentHashMap<>(64);

    public static void saveClientInfo(SocketIOClient socketClient, ClientInfo clientInfo) {
        CLIENT_STORE.put(socketClient, clientInfo);
    }

    public static void delClientInfo(SocketIOClient socketClient) {
        CLIENT_STORE.remove(socketClient);
    }

    public static ClientInfo getClientInfo(SocketIOClient socketClient) {
        return CLIENT_STORE.get(socketClient);
    }

}
