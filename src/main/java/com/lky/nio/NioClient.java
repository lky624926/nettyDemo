package com.lky.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * nio客户端
 */
public class NioClient {
    private static final String HOST = "127.0.0.1";
    private static final Integer PORT = 8888;

    public static void main(String[] args) {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            boolean connect = socketChannel.connect(new InetSocketAddress(HOST, PORT));
            ByteBuffer allocate = ByteBuffer.allocate(1024);
            byte[] bytes1 = "hello world".getBytes();
            allocate.put(bytes1);
            allocate.flip();
            socketChannel.write(allocate);
            allocate.clear();
            socketChannel.read(allocate);
            byte[] bytes = new byte[1024];
            allocate.get(bytes);
            String string = new String(bytes);
            System.out.println(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
