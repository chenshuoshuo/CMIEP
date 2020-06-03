package com.lqkj.web.cmiep.modules.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lqkj.web.cmiep.message.MessageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author wells
 * @ClassNmae WebSocketHandler
 * @Description TODO
 * @Date 2020-05-27 10:48
 * @Version 1.0
 **/
@Component
public class WebSocketPushHandler extends TextWebSocketHandler {
    Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final static List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<WebSocketSession>());
    private final static HashMap<String,WebSocketSession > WEB_SOCKET_SESSION_HASH_MAP = new HashMap<>();
    //接收文本消息，并发送出去
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        LOGGER.info(message.getPayload());
        try {
            JSONObject parse = JSON.parseObject(message.getPayload());
            String uid = parse.getString("uid");
            WEB_SOCKET_SESSION_HASH_MAP.put(uid,session);
            TextMessage sendMsg = new TextMessage(JSON.toJSONString(MessageBean.ok("通讯建立成功"),SerializerFeature.WriteNullStringAsEmpty));
            session.sendMessage(sendMsg);
        }catch (Exception e){
            TextMessage sendMsg = new TextMessage(JSON.toJSONString(MessageBean.error(e.getMessage()),SerializerFeature.WriteNullStringAsEmpty));
            session.sendMessage(sendMsg);
            e.printStackTrace();
        }



    }

    public Boolean pushMsg(String uid,String msg) {
        WebSocketSession webSocketSession = WEB_SOCKET_SESSION_HASH_MAP.get(uid);
        if(webSocketSession==null){
            LOGGER.info("消息推送失败");
            return false;
        }
        if(sessions.contains(webSocketSession)){
            TextMessage sendMsg = new TextMessage(JSON.toJSONString(MessageBean.ok(msg),SerializerFeature.WriteNullStringAsEmpty));
            try {
                webSocketSession.sendMessage(sendMsg);
                return true;
            }catch (IOException e){
                LOGGER.info("消息推送失败",e.getMessage());
                return false;
            }
        }
        LOGGER.info("消息推送失败，连接已断开");
        return false;
    }
    //连接建立后处理
    @SuppressWarnings("unchecked")
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        LOGGER.info("建立连接");
        LOGGER.info(sessions.size()+"");
        //处理离线消息
    }
    //抛出异常时处理
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if(session.isOpen()){
            session.close();
        }
        sessions.remove(session);
        LOGGER.info("连接异常");
    }
    //连接关闭后处理
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
        LOGGER.info("连接关闭");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
