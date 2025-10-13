package com.supremesolutions.channel_server.dto;

public record ChannelMessage(String from, String channelId, String content, long timestamp) {}
