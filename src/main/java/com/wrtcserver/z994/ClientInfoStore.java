package com.wrtcserver.z994;

import ch.qos.logback.core.net.server.Client;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.wrtcserver.z994.dto.ClientInfo;
import jodd.cli.Cli;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储room相关的信息，例如room里的所有client信息
 *
 * @author Zexho
 * @date 2020/10/21 10:04 上午
 */
@Slf4j
@Component
public class ClientInfoStore {

    private static final Map<String, LinkedList<ClientInfo>> ROOM_CLIENT = new ConcurrentHashMap<>(16);
    private static final Map<String, SocketIOClient> ACCOUNT_CLIENT = new ConcurrentHashMap<>(16);

    private ClientInfoStore() {
    }

    /**
     * 获取房间客户端
     *
     * @param roomId 房间id
     * @return 房间id里面所有client信息
     */
    public static List<ClientInfo> getClients(String roomId) {
        return ROOM_CLIENT.get(roomId);
    }

    public static SocketIOClient getClientByAccount(String account) {
        SocketIOClient clientInfo = ACCOUNT_CLIENT.get(account);
        assert clientInfo != null;
        return clientInfo;
    }

    /**
     * 添加房间客户端
     */
    public static void addRoomIdAndClient(String roomId, ClientInfo client) {
        if (ROOM_CLIENT.containsKey(roomId)) {
            ROOM_CLIENT.get(roomId).add(client);
            return;
        }
        LinkedList<ClientInfo> clientInfos = new LinkedList<>();
        clientInfos.add(client);
        ROOM_CLIENT.put(roomId, clientInfos);
    }

    public static void addAccountAndClientInfo(String account, SocketIOClient socketClient) {
        ACCOUNT_CLIENT.put(account, socketClient);
    }

    /**
     * 移除房间里session对应的client
     *
     * @param roomId    房间id
     * @param sessionId 会话id
     */
    public static ClientInfo removeClientByRoomId(String roomId, UUID sessionId) {
        if (!ROOM_CLIENT.containsKey(roomId)) {
            return null;
        }

        // todo 存在线程安全：clientInfos
        List<ClientInfo> clientInfos = ROOM_CLIENT.get(roomId);
        for (int i = 0; i < clientInfos.size(); i++) {
            if (clientInfos.get(i).getSessionId().equals(sessionId)) {
                return clientInfos.remove(i);
            }
        }

        return null;
    }

    public static SocketIOClient removeClientByAccount(String account) {
        return ACCOUNT_CLIENT.remove(account);
    }

    public static void setClientByAccount(String account, ClientInfo clientInfo) {

    }
}