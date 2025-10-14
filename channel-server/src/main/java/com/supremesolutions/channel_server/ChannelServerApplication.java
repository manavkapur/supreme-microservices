package com.supremesolutions.channel_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ChannelServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChannelServerApplication.class, args);
    }
}
