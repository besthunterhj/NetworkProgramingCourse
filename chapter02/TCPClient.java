/*
Author: 王俊朗
Class: 软件工程1803
ID: 20181003043
 */
package chapter02;

import java.net.Socket;
import java.io.*;

public class TCPClient {
    //定义Socket用于联网
    private Socket socket;

    //定义字符的输入输出流，使用BufferedReader减轻阻塞影响
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    public TCPClient(String IPAddress, String port) throws IOException
    {
        //向服务器发起连接，实现TCP的三次握手过程；若不成功，则抛出错误信息

        //将之前定义的socket实例化，并为其增加IP地址和端口参数，同时将UI界面接收的端口号转换为整数类型
        socket = new Socket(IPAddress, Integer.parseInt(port));

        //通过调用socket.getOutputStream获取字节输出流
        OutputStream socketOut = socket.getOutputStream();

        //封装字节输出流为网络输出字符流，将其字符编码为utf-8，并显式自动进行flush
        printWriter = new PrintWriter(
                new OutputStreamWriter(
                        socketOut, "utf-8"), true
                );

        //通过调用socket.getOutputStream获取字节输出流
        InputStream socketIn = socket.getInputStream();

        //封装字节输入流为网络输入字符流
        bufferedReader = new BufferedReader(new InputStreamReader(socketIn, "utf-8"));
    }

    //实现发送信息的功能
    public void send(String message)
    {
        //将message输入到printWriter输出流中，并发送出去
        printWriter.println(message);
    }

    //实现接收信息的功能
    public String receive()
    {
        //定义一个message字符串用于读取收到的字符串
        String message = null;

        try
        {
            //从bufferedReader网络字符输入流中读取信息，且每次只能接受一行信息，若不够一行则语句阻塞
            message = bufferedReader.readLine();
        }
        catch (IOException exception)
        {
            //捕获错误信息
            exception.printStackTrace();
        }
        //返回接收到的信息用于在UI界面输出
        return message;
    }

    //实现关闭网络连接的功能
    public void close()
    {
        try
        {
            if (socket != null)
            {
                //关闭网络连接及相关的输入输出流，实现TCP的四次握手断开
                socket.close();
            }
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }



    public static void main(String[] args) throws IOException
    {
        TCPClient tcpClient = new TCPClient("127.0.0.1", "8008");
        tcpClient.send("hello");

        System.out.println(tcpClient.receive());
    }
}

