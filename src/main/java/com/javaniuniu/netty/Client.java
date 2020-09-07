package com.javaniuniu.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.Socket;

/**
 * @auther: javaniuniu
 * @date: 2020/9/7 10:14 AM
 * Netty默认多线程
 */
public class Client {
    public static void main(String[] args) {
        //事件处理线程池
        EventLoopGroup group = new NioEventLoopGroup(1);//1表示一个线程
        Bootstrap b = new Bootstrap();
        try {
            b.group(group)
                    .channel(NioSocketChannel.class)//线程类型
                    .handler(new ClientChannelInitializer())
                    .connect("localhost",8888)
                    .sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();//关闭客户端
        }

    }


}

class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        System.out.println(ch);
    }
}
