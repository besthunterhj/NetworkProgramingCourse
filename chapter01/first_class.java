/*
Author: 王俊朗
Class: 软件工程1803
ID: 20181003043
 */

package chapter01;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Scanner;

public class first_class extends Application {

    //新建四个功能按钮
    private Button btnExit = new Button("退出");
    private Button btnSend = new Button("发送");
    private Button btnOpen = new Button("加载");
    private Button btnSave = new Button("保存");

    //新建发送信息的文本框及其名字
    private TextField tfSend = new TextField();
    private Label tfSendName = new Label("信息输入区：");

    //新建显示信息的文本框
    private TextArea taDisplay = new TextArea();
    private Label taDisplayName = new Label("信息显示区：");

    private TextFileIO textFileIO = new TextFileIO();

    @Override
    public void start(Stage primaryStage)
    {
        //整体布局为网格布局
        GridPane mainPane = new GridPane();

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
        vbox.getChildren().addAll(taDisplayName, taDisplay, tfSendName, tfSend);

        //将vbox放入主布局并居中
        mainPane.add(vbox, 0, 0);
        mainPane.setAlignment(Pos.CENTER);

        //新建底部区域
        HBox hBox = new HBox();

        //设置底部区域各按钮之间的间隔
        hBox.setSpacing(10);

        //与主题区域统一空隙
        hBox.setPadding(new Insets(10,20, 10, 20));

        //将按钮放入hbox中
        hBox.getChildren().addAll(btnSend, btnSave, btnOpen, btnExit);

        //四个按钮设置为右居中
        hBox.setAlignment(Pos.CENTER_RIGHT);

        //将底部区域放进主布局中
        mainPane.add(hBox, 0, 1);

        //实现退出事件
        btnExit.setOnAction(event ->
                {
                    System.exit(0);
                }
                );

        //实现点击按钮发送事件
        btnSend.setOnAction(event ->
        {
            String message = tfSend.getText();
            taDisplay.appendText(message + "\n");
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
                        taDisplay.appendText(message + "\n");
                        tfSend.clear();
                    }

                }
                );

        //实现点击保存按钮的功能
        btnSave.setOnAction(event ->
                {
                    textFileIO.append(
                            LocalDateTime.now().withNano(0) + " " + taDisplay.getText()
                    );
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("提示");
                    alert.setHeaderText("保存成功！");
                    alert.setContentText("");
                    alert.showAndWait();
                }
                );

        //实现点击加载按钮的功能
        btnOpen.setOnAction(event ->
                {
                    String message = textFileIO.load();
                    if (message != null)
                    {
                        taDisplay.clear();
                        taDisplay.setText(message);
                    }
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


class TextFileIO
{
    //定义一个PrintWriter成员变量帮助输入
    private PrintWriter pw = null;

    //定义一个Scanner成员变量帮助输出
    private Scanner sc = null;

    //构造函数
    public TextFileIO()
    {}

    //实现保存的功能函数
    public void append(String message)
    {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);

        if(file == null)
        {
            return;
        }

        try
        {
            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
            pw.println(message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            pw.close();
        }
    }

    public String load()
    {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        if (file == null)
        {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();

        try
        {
            sc = new Scanner(file, "utf-8");
            while (sc.hasNext())
            {
                stringBuilder.append(sc.nextLine() + "\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            sc.close();
        }
        return stringBuilder.toString();
    }
}
