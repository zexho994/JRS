package com.wrtcserver.z994;

import com.wrtcserver.z994.dto.ClientInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储room相关的信息，例如room里的所有client信息
 * @author Zexho
 * @date 2020/10/21 10:04 上午
 */
@Slf4j
@Component
public class RoomInfoStore {

    private static final Map<String, List<ClientInfo>> ROOM_CLIENT = new ConcurrentHashMap<>();

    private RoomInfoStore(){};

    /**
     * 添加房间客户端
     */
    public static void addRoomClient(String roomId, ClientInfo client) {
        if (ROOM_CLIENT.containsKey(roomId)) {
            ROOM_CLIENT.get(roomId).add(client);
            return;
        }
        ArrayList<ClientInfo> clientInfos = new ArrayList<>();
        clientInfos.add(client);
        ROOM_CLIENT.put(roomId, clientInfos);
    }

    /**
     * 获取房间客户端
     *
     * @param roomId 房间id
     * @return 房间id里面所有client信息
     */
    public static List<ClientInfo> getRoomClients(String roomId) {
        return ROOM_CLIENT.get(roomId);
    }


}