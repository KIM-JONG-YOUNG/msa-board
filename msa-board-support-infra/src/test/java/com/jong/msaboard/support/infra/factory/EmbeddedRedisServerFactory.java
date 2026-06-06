package com.jong.msaboard.support.infra.factory;

import java.io.IOException;
import java.net.ServerSocket;
import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;
import redis.embedded.util.Architecture;
import redis.embedded.util.OS;

public class EmbeddedRedisServerFactory {

    public static RedisServer create() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return RedisServer.builder()
                .redisExecProvider(RedisExecProvider.defaultProvider()
                    .override(OS.MAC_OS_X, Architecture.x86_64, "redis/embedded-redis-server")
                    .override(OS.MAC_OS_X, Architecture.x86, "redis/embedded-redis-server"))
                .port(socket.getLocalPort())
                .build();
        } catch (IOException e) {
            throw new RuntimeException("Embedded Redis Server를 실행하는데 오류가 발생했습니다.", e);
        }
    }

}
