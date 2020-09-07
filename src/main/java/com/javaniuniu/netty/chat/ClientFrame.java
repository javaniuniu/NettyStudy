package com.javaniuniu.netty.chat;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @auther: javaniuniu
 * @date: 2020/9/7 1:11 PM
 */
public class ClientFrame extends Frame {
    public static final  ClientFrame INSTANCE = new ClientFrame();

    private TextArea ta = new TextArea();
    private TextField tf = new TextField();

    private ChatClient chatClient= null;

    public ClientFrame() {
        this.setSize(600, 400);
        this.setLocation(100, 20);
        this.add(ta, BorderLayout.CENTER);
        this.add(tf, BorderLayout.SOUTH);
        tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 把字符串发射到服务器
                chatClient.send(tf.getText());
//                ta.setText(ta.getText() + tf.getText());
                tf.setText("");
            }
        });
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                chatClient.closeConnect();
                System.exit(0);
            }

        });
    }

    public void connectToServer() {
        chatClient = new ChatClient();
        chatClient.connect();
    }
    public void updateText(String msgAccepted) {
        this.ta.setText(ta.getText() + System.getProperty("line.separator") + msgAccepted);
    }

    public static void main(String[] args) {
        ClientFrame frame = ClientFrame.INSTANCE;
        frame.setVisible(true);
        frame.connectToServer();
    }
}




