package com.wrtcserver.z994.socket;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zexho
 * @date 2020/10/20 6:27 下午
 */
@Slf4j
@Service(value = "socketService")
public class SocketServiceImpl implements ISocketService {

    public static Map<UUID, String> session = new ConcurrentHashMap<>();

    private SocketIOServer socketServer;
    private final SocketListener listener;

    @Autowired
    public SocketServiceImpl(SocketIOServer socketServer, SocketListener listener) {
        this.socketServer = socketServer;
        this.listener = listener;
    }

    /**
     * Spring IoC容器创建之后，在加载SocketIOServiceImpl Bean之后启动
     */
    @PostConstruct
    private void autoStartup() {
        log.info("start socket-io");
        start();
    }

    /**
     * Spring IoC容器在销毁SocketIOServiceImpl Bean之前关闭,避免重启项目服务端口占用问题
     */
    @PreDestroy
    private void autoStop() {
        log.info("stop socket-io");
        stop();
    }

    @Override
    public void start() {
        // 添加监听事件
        listener.connect(socketServer);
        listener.avShare(socketServer);
        listener.avShareToAccount(socketServer);
        listener.screenShare(socketServer);
        listener.answer(socketServer);
        listener.offer(socketServer);
        listener.joinRoom(socketServer);
        listener.iceCandidate(socketServer);
        listener.leaveRoom(socketServer);
        listener.disConnect(socketServer);

        // 启动服务
        socketServer.start();

    }

    @Override
    public void stop() {
        if (socketServer != null) {
            socketServer.stop();
            socketServer = null;
        }
    }

}
