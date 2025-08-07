package com.dungi.apiserver.presentation.memo.handler;

import com.dungi.common.event.MemoCreateEvent;
import com.dungi.common.event.MemoDeleteEvent;
import com.dungi.common.event.MemoEditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemoEventHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleMemoEdited(MemoEditEvent event) {
        messagingTemplate.convertAndSend("/topic/room/" + event.getRoomId() + "/memo/" + event.getMemoId() + "/edit", event);
    }

    @EventListener
    public void handleMemoCreated(MemoCreateEvent event) {
        messagingTemplate.convertAndSend("/topic/room/" + event.getRoomId() + "/memo/create", event);
    }

    @EventListener
    public void handleMemoDeleted(MemoDeleteEvent event) {
        messagingTemplate.convertAndSend("/topic/room/" + event.getRoomId() + "/memo/delete", event);
    }
}