package com.lky.nio;

import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * nio客户端
 */
public class NioClient {
    private static final String HOST = "127.0.0.1";
    private static final String PORT = "8888";
    private static Selector selector;

    static {
        SocketChannel open = SocketChannel.open();
        open.connect(new )
    }
}
