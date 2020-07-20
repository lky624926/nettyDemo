package com.lky.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class IoClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8888);
            OutputStream outputStream = socket.getOutputStream();
            String s = "hello world";
            outputStream.write(s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
