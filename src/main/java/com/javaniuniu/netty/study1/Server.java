package com.javaniuniu.netty.study1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @auther: javaniuniu
 * @date: 2020/9/7 10:21 AM
 */
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress("127.0.0.1", 8888));
        Socket s = ss.accept();//阻塞方法
        System.out.println("a client connect");

    }
}
