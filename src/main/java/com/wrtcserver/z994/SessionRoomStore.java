package com.wrtcserver.z994;

import com.wrtcserver.z994.dto.ClientInfo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储会话session和room信息
 *
 * @author Zexho
 * @date 2020/10/22 3:21 下午
 */
public class SessionRoomStore {

    /**
     * K:session ，V：roomId
     */
    private static final Map<UUID, String> SESSION_ROOM_MAP = new ConcurrentHashMap<>(16);

    private SessionRoomStore() {
    }

    /**
     * 添加session和roomId信息
     *
     * @param session 会话Id {@link ClientInfo#getSessionId()}
     * @param roomId  房间id {@link ClientInfo#getRoomId()}
     */
    public static void put(UUID session, String roomId) {
        SESSION_ROOM_MAP.put(session, roomId);
    }

    /**
     * 查询roomId
     *
     * @param session 会话id
     * @return roomId
     */
    public static String getRoomId(UUID session) {
        return SESSION_ROOM_MAP.get(session);
    }

}
