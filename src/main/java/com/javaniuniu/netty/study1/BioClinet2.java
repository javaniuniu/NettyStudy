package com.javaniuniu.netty.study1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @auther: javaniuniu
 * @date: 2020/9/7 10:44 AM
 * netty中所有的方法都是异步的，
 * 需要使用 sync() 达到阻塞的效果，直到连上客户的，或者出来结果为止
 * 要么使用监听，通过状态来判断，以做相应的处理
 *
 */
public class BioClinet2 {
    public static void main(String[] args) {
        //事件处理线程池
        EventLoopGroup group = new NioEventLoopGroup(1);//1表示一个线程
        Bootstrap b = new Bootstrap();
        try {
            ChannelFuture f = b.group(group)
                    .channel(NioSocketChannel.class)//线程类型
                    .handler(new ClientChannelInitializer2())
                    .connect("localhost", 8888);
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!f.isSuccess()) {
                        System.out.println("not connect...");
                    } else {
                        System.out.println("connect success!");
                    }
                }
            });
            f.sync();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();//关闭客户端
        }

    }


}

class ClientChannelInitializer2 extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        System.out.println(ch);
    }
}