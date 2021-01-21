/*
Author: 王俊朗
Class: 软件工程1803
ID: 20181003043
 */
package chapter02;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    //定义端口号，为服务器监听的端口号
    private int port = 8008;

    //定义服务器的Socket
    private ServerSocket serverSocket;

    public TCPServer() throws IOException
    {
        //让Socket设置为在8008端口上
        serverSocket = new ServerSocket(port);
        System.out.println("服务器启动监听在 " + port + " 端口");
    }

    private PrintWriter getWriter(Socket socket) throws IOException
    {
        //获取字节输出流
        OutputStream socketOut = socket.getOutputStream();

        //返回一个网络字符输出流
        return new PrintWriter(
                new OutputStreamWriter(socketOut, "utf-8"), true
        );
    }

    private BufferedReader getReader(Socket socket) throws IOException
    {
        //获取字节输入流
        InputStream socketIn = socket.getInputStream();

        //返回一个网络字符输入流
        return new BufferedReader(
                new InputStreamReader(socketIn, "utf-8")
        );
    }

    //不设置并发，使得此服务器只能与一个客户进行通信连接
    public void Service()
    {
        //使服务器保持运行
        while(true)
        {
            Socket socket = null;

            try
            {
                //程序处于阻塞状态，并监听端口，直到有客户连接；若有连接就生产一个Socket
                socket = serverSocket.accept();

                //本地服务器控制台显示客户端连接的用户信息
                System.out.println("有新的连接申请： " + socket.getInetAddress());

                //定义字符串输入流
                BufferedReader bufferedReader = getReader(socket);

                //定义字符串输出流
                PrintWriter printWriter = getWriter(socket);

                //若客户端正常连接成功，则发送服务器的欢迎信息作为特征，然后等待客户端发送信息
                printWriter.println("来自本服务器： Welcome to dear guest!");


                String message = "";

                //此处程序处于阻塞状态，每次都从输入流中读入一行字符串
                while ((message = bufferedReader.readLine()) != null)
                {
                    if (message.equals("bye"))
                    {
                        //向输出流中输出一行字符串，远程客户端可以读取该字符串
                        printWriter.println("来自本服务器： 服务器断开连接，感谢使用本服务！");

                        //在控制台显示客户端断开连接
                        System.out.println("客户端已离开！");
                        break;
                    }

                    if (message.contains("吗?"))
                    {
                        String[] stringArray = message.split("吗");

                        if (stringArray[0].contains(","))
                        {
                            String[] strings = stringArray[0].split(",");
                            String temp = strings[1];
                            printWriter.println("来自本服务器：" + temp);
                        }

                        int length = stringArray[0].length();
                        String result = stringArray[0].substring(length - 1);
                        printWriter.println("来自本服务器：" + result);

                    }

                    //向输出流中输出一行字符串，远程客户端可以读取该字符串
                    printWriter.println("来自本服务器： " + message);
                }
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
            }
            finally
            {
                try
                {
                    if (socket != null)
                    {
                        //关闭socket连接及其相关的输入输出流
                        socket.close();
                    }
                }
                catch (IOException IOexception)
                {
                    IOexception.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException
    {
        //启动服务
        new TCPServer().Service();
    }
}
