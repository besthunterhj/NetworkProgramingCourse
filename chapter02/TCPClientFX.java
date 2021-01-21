/*
Author: 王俊朗
Class: 软件工程1803
ID: 20181003043
 */

package chapter02;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;


public class TCPClientFX extends Application {

    //实例化客户端
    private TCPClient tcpCilent;

    //设置一个是否已连接的成员变量
    private boolean isConnected = false;

    //新建3个功能按钮
    private Button btnExit = new Button("退出");
    private Button btnSend = new Button("发送");
    private Button btnLink = new Button("连接");

    //新建发送信息的文本框及其名字
    private TextField tfSend = new TextField();
    private Label tfSendName = new Label("信息输入区：");

    //新建显示信息的文本框及其名字
    private TextArea taDisplay = new TextArea();
    private Label taDisplayName = new Label("信息显示区：");

    //新建ip地址框及其名字
    private TextField tfIpAddress = new TextField();
    private Label tfIpAddressName = new Label("IP地址：");

    //新建端口框及其名字
    private TextField tfPort = new TextField();
    private Label tfPortName = new Label("端口：");


    //将连接服务器作为一个函数
    public boolean connect(String ip, String port)
    {
        try
        {
            tcpCilent = new TCPClient(ip, port);

            //成功连接到服务器后，接收服务器发来的欢迎信息
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setHeaderText("连接成功！");
            alert.setContentText("");
            alert.showAndWait();
            return true;
        }
        catch (IOException exception)
        {
            //若连接失败，在信息显示区输出错误信息
            taDisplay.appendText("服务器连接失败！" + "\n");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("错误");
            alert.setHeaderText("连接失败！ 请确认ip地址和端口号");
            alert.setContentText("");
            alert.showAndWait();
            exception.printStackTrace();
        }
        return false;
    }



    @Override
    public void start(Stage primaryStage)
    {
        //整体布局为网格布局
        GridPane mainPane = new GridPane();

        //新建一个HBox用于放置输入Socket的一栏
        HBox top = new HBox();
        top.getChildren().addAll(tfIpAddressName, tfIpAddress, tfPortName, tfPort, btnLink);

        top.setSpacing(10);
        top.setAlignment(Pos.CENTER);

        //定义一个垂直方格用于放文本框及其名字
        VBox vbox = new VBox();

        //设置控件之间的间隔
        vbox.setSpacing(10);

        //设置主体部分的空隙区域
        vbox.setPadding(new Insets(10, 20, 10, 20));

        //信息显示区设置为自动换行和只读
        taDisplay.setWrapText(true);
        taDisplay.setEditable(false);

        //将之前定义的文本框及其名字放进vbox
        vbox.getChildren().addAll(top, taDisplayName, taDisplay, tfSendName, tfSend);

        //新建底部区域
        HBox hBox = new HBox();

        //设置底部区域各按钮之间的间隔
        hBox.setSpacing(10);

        //与主题区域统一空隙
        hBox.setPadding(new Insets(10,20, 10, 20));

        //将按钮放入hbox中
        hBox.getChildren().addAll(btnSend,  btnExit);

        //四个按钮设置为右居中
        hBox.setAlignment(Pos.CENTER_RIGHT);

        //将顶、中、底部区域放进主布局中并居中
        mainPane.add(vbox, 0, 0);
        mainPane.add(hBox, 0, 1);
        mainPane.setAlignment(Pos.CENTER);

        //一开始由于未连接服务器 禁止发送信息
        btnSend.setDisable(true);

        //实现点击按钮发送事件
        btnSend.setOnAction(event ->
                {
                    String message = tfSend.getText();

                    //实现向服务器发送一串字符串信息
                    tcpCilent.send(message);
                    taDisplay.appendText("客户端发送："+ message + "\n");

                    //从服务器接收一行字符串
                    String receiveMessage = tcpCilent.receive();
                    taDisplay.appendText(receiveMessage + "\n");

                    tfSend.clear();
                }
        );

        //实现回车键发送事件
        mainPane.setOnKeyPressed(event ->
                {
                    if (event.getCode() == KeyCode.ENTER)
                    {
                        String message = tfSend.getText();
                        if (event.isShiftDown())
                        {
                            taDisplay.appendText("echo: ");
                        }

                        if (isConnected)
                        {
                            tcpCilent.send(message);
                            taDisplay.appendText("客户端发送："+ message + "\n");

                            if (message.equals("bye"))
                            {
                                btnSend.setDisable(true);
                                btnLink.setDisable(false);
                            }

                            //从服务器接收一行字符串
                            String receiveMessage = tcpCilent.receive();
                            taDisplay.appendText(receiveMessage + "\n");
                        }

                        else
                        {
                            taDisplay.appendText("客户端发送离线信息： " + message + "\n");
                        }

                        tfSend.clear();
                    }
                }
        );

        //实现连接功能
        btnLink.setOnAction(event ->
                {
                    //接收IP地址文本框内的信息
                    String IPAddress = tfIpAddress.getText().trim();

                    //接收端口文本框内的信息
                    String port = tfPort.getText().trim();


                    //实例化客户端类，并尝试连接服务器
                    isConnected = connect(IPAddress, port);

                    //若连接成功但未退出连接 则禁止再发送其它连接申请，并恢复发送按钮
                    btnSend.setDisable(false);
                    btnLink.setDisable(true);

                    //连接成功，发送欢迎语作为特征
                    String welcomeMessage = tcpCilent.receive();
                    taDisplay.appendText(welcomeMessage + "\n");
                }
        );

        //实现退出事件
        btnExit.setOnAction(event ->
                {
                    if (tcpCilent != null)
                    {
                        //向服务器发送关闭连接的特征信息
                        tcpCilent.send("bye");
                        tcpCilent.close();
                        isConnected = false;
                    }
                    System.exit(0);
                }
        );

        //实现关闭窗体退出事件
        primaryStage.setOnCloseRequest(event ->
                    {
                        if (tcpCilent != null)
                        {
                            //向服务器发送关闭连接的特征信息
                            tcpCilent.send("bye");
                            tcpCilent.close();
                            isConnected = false;
                        }
                        System.exit(0);
                    }
                );

        Scene scene = new Scene(mainPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

