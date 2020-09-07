package com.javaniuniu.io.bio;

import com.sun.prism.shader.Solid_TextureYV12_AlphaTest_Loader;

import java.io.IOException;
import java.net.Socket;

/**
 * @auther: javaniuniu
 * @date: 2020/9/7 8:59 AM
 */
public class Client {
    public static void main(String[] args) throws IOException {
        Socket s = new Socket("127.0.0.1",8888);
        s.getOutputStream().write("hello".getBytes());
        s.getOutputStream().flush();
//        s.getOutputStream().close();
        System.out.println("writer over,waiting for msg back,,,");
        byte[] bytes = new byte[1024];
        int len = s.getInputStream().read(bytes);
        System.out.println(new String(bytes, 0, len));
        s.close();




    }
}
