package com.javaniuniu.netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;


/**
 * @auther: javaniuniu
 * @date: 2020/9/7 10:14 AM
 * Netty默认多线程
 */
public class ChatClient {
    private Channel channel = null;

    public void connect() {
        //事件处理线程池
        EventLoopGroup group = new NioEventLoopGroup(1);//1表示一个线程
        Bootstrap b = new Bootstrap();
        try {
            ChannelFuture f = b.group(group)
                    .channel(NioSocketChannel.class)//线程类型
                    .handler(new ClientChannelInitializer())
                    .connect("localhost", 8888);
            f.addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    System.out.println("not connect...");
                } else {
                    System.out.println("connect success!");
                    //channel initial
                    channel = future.channel();

                }
            });
            f.sync();

            System.out.println("client started!");
            f.channel().closeFuture().sync(); // close() -> ChannelFuture closeFuture()会一直阻塞
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();//关闭客户端
        }
    }

    public void send(String msg) {
        ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
        channel.writeAndFlush(buf);
    }

    public static void main(String[] args) {
        ChatClient c = new ChatClient();
        c.connect();
    }


    public void closeConnect() {
        this.send("_bye_");
    }
}

class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ClientChildHandler());
    }
}

class ClientChildHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            String msgAccepted = new String(bytes);
            ClientFrame.INSTANCE.updateText(msgAccepted);

//            System.out.println(buf);
//            System.out.println(buf.refCnt());

        } finally {
            if (buf != null) ReferenceCountUtil.release(buf);
//            System.out.println(buf.refCnt());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // channel 第一次连上可用，写出一个字符串 Direct Memory
        ByteBuf bf = Unpooled.copiedBuffer("hello".getBytes());//ByteBuf 直接访问系统内存 所以速度很快
        ctx.writeAndFlush(bf);// writeAndFlush执行完来之后  会自动清理该系统内存
    }
}