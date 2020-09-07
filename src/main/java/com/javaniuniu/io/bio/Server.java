package com.javaniuniu.io.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @auther: javaniuniu
 * @date: 2020/9/7 9:04 AM
 * bio blocking io 阻塞io，没有新的信息就会一直阻塞
 * 所以bio必须多线程，因为只有一个线程的话，在当前线程没有完成的情况下，其他连接会一直被阻塞
 * 所以这种模型用线程池也没用，
 * 所以可能的使用场景是，点对点传数据，开一两个线程就可以了
 */
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress("127.0.0.1",8888));
        while (true){
            Socket s = ss.accept();//阻塞方法
            new Thread(()->{
                handle(s);
            }).start();

        }
    }

    private static void handle(Socket s) {

        try {
            byte[] bytes = new byte[1024];
            int len = s.getInputStream().read(bytes);
            System.out.println(new String(bytes, 0, len));

            s.getOutputStream().write(bytes,0,len);
            s.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
