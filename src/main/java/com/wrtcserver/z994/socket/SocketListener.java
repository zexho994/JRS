package com.wrtcserver.z994.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.wrtcserver.z994.RoomInfoStore;
import com.wrtcserver.z994.SessionRoomStore;
import com.wrtcserver.z994.dto.ClientInfo;
import com.wrtcserver.z994.dto.SignalMsg;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Zexho
 * @date 2020/10/21 9:39 上午
 */
@Log4j2
@Component
public class SocketListener {

    /**
     * 连接事件
     */
    public void connect(SocketIOServer server) {
        server.addConnectListener(client -> {
            log.info(client.getRemoteAddress() + " web客户端接入 " + client.getSessionId());

            client.sendEvent("init-room");
        });
    }

    /**
     * 断开连接事件
     */
    public void disConnect(SocketIOServer server) {
        server.addDisconnectListener(client -> {
            log.info(client.getRemoteAddress() + "断开连接" + client.getSessionId());

        });
    }

    /**
     * 客户端加入房间
     */
    public void joinRoom(SocketIOServer server) {
        server.addEventListener("join", SignalMsg.class, (socketClient, o, ackRequest) -> {
            log.info("account:{} begin to join room:{}.", o.getAccount(), o.getRoomId());

            // 绑定session & roomId
            ClientInfo clientInfo = new ClientInfo(o.getAccount(), socketClient.getSessionId(), o.getRoomId(), false, false, false);
            SessionRoomStore.put(clientInfo.getSessionId(), o.getRoomId());

            // 添加客户端到房间
            socketClient.joinRoom(o.getRoomId());
            RoomInfoStore.addClient(o.getRoomId(), clientInfo);
            List<ClientInfo> roomClients = RoomInfoStore.getClients(o.getRoomId());

            // 返回joined信息给客户端
            socketClient.getNamespace().getRoomOperations(o.getRoomId()).sendEvent("joined", roomClients, o.getAccount());
        });
    }

    /**
     * 客户端离开房间,删除客户端在房间的记录
     */
    public void leaveRoom(SocketIOServer server) {
        server.addEventListener("leave", SignalMsg.class, (socketClient, o, ackRequest) -> {
            log.info("account:{} leave room :{}.", o.getAccount(), o.getRoomId());

        });
    }

    public void shareRoom(SocketIOServer server){

    }

}
