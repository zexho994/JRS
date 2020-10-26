package com.wrtcserver.z994.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wrtcserver.z994.ClientInfoStore;
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
    private static final Gson GSON = new Gson();
    private static final JsonParser JSON_PARSER = new JsonParser();

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
            // 获取session的roomId
            String roomId = SessionRoomStore.getRoomId(client.getSessionId());
            if (roomId == null) {
                log.warn("session:{} 没有对应的 roomId", client.getSessionId());
                return;
            }

            // 获取客户端信息
            List<ClientInfo> clients = ClientInfoStore.getClients(roomId);
            if (clients == null) {
                log.warn("roomId {} 没有客户端信息", roomId);
                return;
            }

            // room 中移除该客户端
            ClientInfo clientInfo = ClientInfoStore.removeClientByRoomId(roomId, client.getSessionId());
            assert clientInfo != null;

            // 移除account和seesion信息
            ClientInfoStore.removeClientByAccount(clientInfo.getAccount());

            clients = ClientInfoStore.getClients(roomId);
            client.leaveRoom(roomId);
            //给其他客户端发送通知
            client.getNamespace().getRoomOperations(roomId).sendEvent("disconnected", clients, clientInfo.getAccount());
        });
    }

    /**
     * 客户端加入房间事件
     */
    public void joinRoom(SocketIOServer server) {
        server.addEventListener("join", SignalMsg.class, (socketClient, o, ackRequest) -> {
            log.info("account:{} begin to join room:{}.", o.getAccount(), o.getRoomId());

            // 绑定session & roomId
            ClientInfo clientInfo = new ClientInfo(o.getAccount(), socketClient.getSessionId(), o.getRoomId(), false, false, false);
            SessionRoomStore.put(clientInfo.getSessionId(), o.getRoomId());

            // 添加客户端到房间
            socketClient.joinRoom(o.getRoomId());
            ClientInfoStore.addRoomIdAndClient(o.getRoomId(), clientInfo);
            List<ClientInfo> roomClients = ClientInfoStore.getClients(o.getRoomId());

            // 保存account和socketClient
            ClientInfoStore.addAccountAndClientInfo(o.getAccount(), socketClient);

            // 返回joined信息广播给房间的所有客户端
            socketClient.getNamespace().getRoomOperations(o.getRoomId()).sendEvent("joined", roomClients, clientInfo);
        });
    }

    /**
     * 音视频共享消息
     */
    public void avShare(SocketIOServer server) {
        server.addEventListener("avShare", SignalMsg.class, (socketClient, o, ackRequest) -> {
            log.info("account:{} share the av in room {}", o.getAccount(), o.getRoomId());
            socketClient.getNamespace().getRoomOperations(o.getRoomId()).sendEvent("avShared", o.getAccount());
        });
    }

    public void avShareToAccount(SocketIOServer server) {
        server.addEventListener("avShareToAccount", Object.class, (socketClient, msg, ackRequest) -> {
            JsonObject jo = JSON_PARSER.parse(GSON.toJson(msg)).getAsJsonObject();

            // 发送端的account
            String source = jo.get("source").getAsString();
            if (source == null || "".equals(source)) {
                log.warn("source为空,sessionID = {}", socketClient.getSessionId());
            }

            // 目标account
            String dest = jo.get("dest").getAsString();
            SocketIOClient client = ClientInfoStore.getClientByAccount(dest);
            assert client != null;

            log.info("account:{} share the av to dest {}", source, dest);
            client.sendEvent("avShared", source);
        });
    }

    public void screenShareToAccount(SocketIOServer server){
        server.addEventListener("screenShareToAccount", Object.class, (socketClient, msg, ackRequest) -> {
            JsonObject jo = JSON_PARSER.parse(GSON.toJson(msg)).getAsJsonObject();

            // 发送端的account
            String source = jo.get("source").getAsString();
            if (source == null || "".equals(source)) {
                log.warn("source为空,sessionID = {}", socketClient.getSessionId());
            }

            // 目标account
            String dest = jo.get("dest").getAsString();
            SocketIOClient client = ClientInfoStore.getClientByAccount(dest);
            assert client != null;

            log.info("account:{} share the av to dest {}", source, dest);
            client.sendEvent("screenShared", source);
        });
    }

    /**
     * 屏幕共享事件
     * 一个房间发起屏幕共享，将消息发送到该房间所有client
     */
    public void screenShare(SocketIOServer server) {
        server.addEventListener("screenShare", SignalMsg.class, (socketClient, o, ackRequest) -> {
            log.info("account:{} share the screen in room {}", o.getAccount(), o.getRoomId());
            socketClient.getNamespace().getRoomOperations(o.getRoomId()).sendEvent("screenShared", o.getAccount());
        });
    }

    /**
     * 客户端离开房间事件
     * 删除客户端在房间的记录
     */
    public void leaveRoom(SocketIOServer server) {
        server.addEventListener("leave", SignalMsg.class, (socketClient, o, ackRequest) -> {
            log.info("account:{} leave room :{}.", o.getAccount(), o.getRoomId());
        });
    }

    /**
     * 接收offer消息
     * 然后将offer信息转发给指定的客户端
     */
    public void offer(SocketIOServer server) {
        server.addEventListener("offer", Object.class, (socketClient, msg, ackRequest) -> {
            // 从参数中获取source
            JsonObject jo = JSON_PARSER.parse(GSON.toJson(msg)).getAsJsonObject();
            String dest = jo.get("dest").getAsString();
            assert dest != null;
            // 发送给单独服务器
            ClientInfoStore.getClientByAccount(dest).sendEvent("offer", msg);
            log.info("account:{} send the offer to  {}", dest, jo.get("source").getAsString());
        });
    }

    /**
     * 接收广播类型的offer
     */
    public void offers(SocketIOServer server) {
        server.addEventListener("offers", Object.class, (socketClient, msg, ackRequest)
                -> broadcastInRoom(socketClient, msg, "offer", true));
    }

    /**
     * 接收answer消息
     * 然后将answer信息转发给指定的客户端
     */
    public void answer(SocketIOServer server) {
        server.addEventListener("answer", Object.class, (socketClient, msg, ackRequest) -> {
            // 从参数中获取source
            JsonObject jo = JSON_PARSER.parse(GSON.toJson(msg)).getAsJsonObject();
            String dest = jo.get("dest").getAsString();
            assert dest != null;
            // 发送给单独服务器
            ClientInfoStore.getClientByAccount(dest).sendEvent("answer", msg);
            log.info("account:{} send the answer to  {}", dest, jo.get("source").getAsString());
        });
    }

    public void iceCandidate(SocketIOServer server) {
        server.addEventListener("ice_candidate", Object.class, (socketClient, msg, ackRequest) -> {
            // 从参数中获取source
            JsonObject jo = JSON_PARSER.parse(GSON.toJson(msg)).getAsJsonObject();
            String dest = jo.get("dest").getAsString();
            assert dest != null;
            // 发送给单独服务器
            ClientInfoStore.getClientByAccount(dest).sendEvent("ice_candidate", msg);
            log.info("account:{} send the ice candidate to  {}", dest, jo.get("source").getAsString());
        });
    }

    /**
     * 房间内广播消息
     *
     * @param client 发消息的客户端
     * @param msg    消息内容
     * @param event  消息事件名
     */
    private void broadcastInRoom(SocketIOClient client, Object msg, String event, boolean exclusiveSelf) {
        Gson gson = new Gson();
        JsonParser jp = new JsonParser();
        JsonObject jo = jp.parse(gson.toJson(msg)).getAsJsonObject();
        log.info("account " + jo.get("source") + "发送 " + event + " 广播消息 to room " + jo.get("roomId").getAsString());
        if (exclusiveSelf) {
            client.getNamespace().getRoomOperations(jo.get("roomId").getAsString()).sendEvent(event, client, msg);
        } else {
            client.getNamespace().getRoomOperations(jo.get("roomId").getAsString()).sendEvent(event, msg);
        }
    }

}
