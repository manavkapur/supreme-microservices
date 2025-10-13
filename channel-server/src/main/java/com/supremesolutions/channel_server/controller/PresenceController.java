package com.supremesolutions.channel_server.controller;

import com.supremesolutions.channel_server.service.ChannelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PresenceController {

    private final ChannelService service;

    public PresenceController(ChannelService service) {
        this.service = service;
    }

    @GetMapping("/api/presence")
    public boolean isOnline(@RequestParam String email) {
        return service.isOnline(email);
    }
}
