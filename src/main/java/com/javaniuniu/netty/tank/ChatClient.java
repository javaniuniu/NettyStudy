package com.javaniuniu.netty.tank;

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

            ClientFrame.INSTANCE.updateText("client started!!");
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
        ch.pipeline()
                .addLast(new TankMsgEncoder()) // 现将编码的hander 做编码处理
                .addLast(new ClientChildHandler()); // 在做相应的业务处理
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
        ctx.writeAndFlush(new TankMsg(5,8));
    }
}