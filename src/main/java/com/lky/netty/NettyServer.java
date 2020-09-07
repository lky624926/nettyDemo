package com.lky.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyServer {

    protected static NioEventLoopGroup bossGroup; //负责处理TCP/IP连接
    protected static NioEventLoopGroup workGroup; //负责处理Channel（通道）的I/O

    private static Integer port = 8081;

    private static void init() throws UnknownHostException {
        bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger(0);
            public Thread newThread(Runnable r) {
                return new Thread(r, "BOSS_" + index.incrementAndGet());
            }
        });
        workGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 10, new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger(0);
            public Thread newThread(Runnable r) {
                return new Thread(r, "WORK_" + index.incrementAndGet());
            }
        });

        InetAddress address = InetAddress.getLocalHost();
        System.setProperty("websocketPath","ws://" + address + ":" + port);
    }


    public static void main(String[] args) {
        try {
            init();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpServerCodec(),   //请求解码器
                                    new HttpObjectAggregator(65536),//将多个消息转换成单一的消息对象
                                    new IdleStateHandler(60, 0, 0), //检测链路是否读空闲
                                    new AuthHandler() //处理握手和认证
                            );
                        }
                    });
            serverBootstrap.bind().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
