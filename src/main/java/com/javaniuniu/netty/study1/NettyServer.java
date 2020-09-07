package com.javaniuniu.netty.study1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @auther: javaniuniu
 * @date: 2020/9/7 11:34 AM
 * 多客户端能同步之间的数据，是通过服务端将客户端传来的数据转发到其他客户端
 */
public class NettyServer {

    // 使用通道组 处理通道上的所有默认事件
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);// 用于客户端连接
        EventLoopGroup workderGroup = new NioEventLoopGroup(2);// 用于事件处理连接
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            ChannelFuture f =  bootstrap.group(bossGroup,workderGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new myChannelInitializer())
                    .bind(8888)
                    .sync();
            System.out.println("server started...");
            f.channel().closeFuture().sync(); // close() -> ChannelFuture closeFuture()会一直阻塞
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workderGroup.shutdownGracefully();
        }


    }
}

class myChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pl = ch.pipeline();
        pl.addLast(new ServerChildHandler());
    }
}
class ServerChildHandler extends ChannelInboundHandlerAdapter{


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyServer.clients.add(ctx.channel());//通道可以用的时候就可以把通道放到通道组里
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf =  (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(),bytes);
            System.out.println(new String(bytes));

//            ctx.writeAndFlush(buf); // 这里有关闭操作 所以 finally 不需要在关闭
            NettyServer.clients.writeAndFlush(buf); // 使用通道组，将所有客户端传递来的数据都传递出去

//            System.out.println(buf);
//            System.out.println(buf.refCnt());

        }finally {
//            if (buf !=null) ReferenceCountUtil.release(buf);
//            System.out.println(buf.refCnt());
        }
    }

    // 当通道上出现任何异常，把ctx关闭掉
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

