package com.lky.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * nio服务端
 */
public class NioChat {

    private static Selector selector;
    private static ServerSocketChannel listenChannel;
    private static final int PORT = 8888;

    static {
        try {
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            listenChannel.configureBlocking(false);
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        while (true){
            int select = selector.select();
            if (select>0){
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                if (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isAcceptable()){
                        //连接事件
                        SocketChannel accept = listenChannel.accept();
                        accept.configureBlocking(false);
                        //获取新的channel并注册为读事件
                        accept.register(selector, SelectionKey.OP_READ);
                    }else if (selectionKey.isReadable()){
                        read(selectionKey);
                    }
                    iterator.remove();
                }
            }
        }
    }



    private static void read(SelectionKey selectionKey){
        try {
            SocketChannel channel = (SocketChannel)selectionKey.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            StringBuilder msg = new StringBuilder();
            for (;;){
                int read = channel.read(buffer);
                if (read>0){
                    byte[] array = buffer.array();
                    String s = new String(array);
                    msg.append(s);
                    System.out.println("收到消息："+s);
                }else {
                    break;
                }
            }
            //向其他用户发消息
            sendMsgToOther(msg.toString(),channel);
        } catch (IOException e) {
            e.printStackTrace();
            selectionKey.cancel();
        }
    }


    private static void sendMsgToOther(String msg,SocketChannel channel){
        Set<SelectionKey> keys = selector.keys();
        //所有注册过的channel
        for (SelectionKey selectionKey:keys){
            try {
                SelectableChannel channel1 = selectionKey.channel();
                if (channel1 instanceof SocketChannel){
                    SocketChannel socketChannel = (SocketChannel)channel1;
                    if (!channel.equals(socketChannel)){
                        ByteBuffer wrap = ByteBuffer.wrap(msg.getBytes());
                        socketChannel.write(wrap);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                selectionKey.cancel();
            }
        }
    }
}
