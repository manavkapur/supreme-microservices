package com.supremesolutions.channel_server.controller;

import com.supremesolutions.channel_server.dto.ChannelMessage;
import com.supremesolutions.channel_server.service.ChannelService;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/channel")
public class ChannelController {

    private final SimpMessagingTemplate template;
    private final ChannelService channelService;

    public ChannelController(SimpMessagingTemplate template, ChannelService channelService) {
        this.template = template;
        this.channelService = channelService;
    }

    @MessageMapping("/channel/{channelId}")
    public void sendToChannel(@DestinationVariable String channelId,
                              ChannelMessage message,
                              SimpMessageHeaderAccessor headerAccessor) {

        template.convertAndSend("/topic/channel." + channelId, message);
        channelService.onMessage(message);
    }

    @PostMapping("/{channelId}/send")
    public void sendRest(@PathVariable String channelId, @RequestBody ChannelMessage message) {
        template.convertAndSend("/topic/channel." + channelId, message);
        channelService.onMessage(message);
    }
}
