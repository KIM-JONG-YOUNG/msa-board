package com.jong.msa.board.common.utils;

import java.io.IOException;
import java.net.ServerSocket;

public final class PortUtils {

    public static int getAvailablePort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            serverSocket.setReuseAddress(true);
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("사용 가능한 포트를 찾을 수 없습니다.", e);
        }
    }

}
