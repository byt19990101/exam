package com.zzxx.exam.ui;

import com.zzxx.exam.contrller.ClientContext;
import com.zzxx.exam.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;

/**
 * 主菜单界面
 */
public class MenuFrame extends JFrame {

    public MenuFrame() {
        init();
    }

    private void init() {
        setTitle("指针信息在线测评");
        setSize(600, 400);
        setContentPane(createContentPane());
        setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

            }
        });
    }

    private JPanel createContentPane() {
        JPanel pane = new JPanel(new BorderLayout());

        ImageIcon icon = new ImageIcon(this.getClass().getResource("pic/title.png"));

        pane.add(BorderLayout.NORTH, new JLabel(icon));

        pane.add(BorderLayout.CENTER, createMenuPane());

        pane.add(BorderLayout.SOUTH, new JLabel("指针信息--版权所有 盗版必究", JLabel.RIGHT));

        return pane;
    }

    private JLabel info; // 记录用户的信息

    private JPanel createMenuPane() {
        JPanel pane = new JPanel(new BorderLayout());
        // 务必将 info 引用到界面控件对象
        info = new JLabel("XXX 同学您好!", JLabel.CENTER);
        pane.add(BorderLayout.NORTH, info);
        pane.add(BorderLayout.CENTER, createBtnPane());

        return pane;
    }

    public void updateView(User user) {
        info.setText(user.getName() + " 同学您好!");
    }


    private ClientContext controller;

    public void setController(ClientContext controller) {
        this.controller = controller;
    }

    private JPanel createBtnPane() {
        JPanel pane = new JPanel(new FlowLayout());
        JButton start = createImgBtn("pic/exam.png", "开始");
        JButton result = createImgBtn("pic/result.png", "分数");
        JButton msg = createImgBtn("pic/message.png", "考试规则");
        JButton exit = createImgBtn("pic/exit.png", "离开");

        pane.add(start);
        pane.add(result);
        pane.add(msg);
        pane.add(exit);

        getRootPane().setDefaultButton(start);

        //进入考试页面
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //通知控制器，开始考试
                try {
                    controller.start();
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
            }
        });

        //进入考试分数页面
        result.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                controller.resultStart();
            }
        });

        //进入考试规则页面
        msg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                controller.msgStart();
            }
        });

        //退出
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        return pane;
    }

    // 创建图片按钮的方法
    private JButton createImgBtn(String img, String txt) {
        ImageIcon ico = new ImageIcon(this.getClass().getResource(img));

        JButton button = new JButton(txt, ico);
        // button.setIcon(ico);
        // 设置文本相对于图标的垂直位置
        button.setVerticalTextPosition(JButton.BOTTOM);
        // 设置文本相对于图标的水平位置
        button.setHorizontalTextPosition(JButton.CENTER);

        return button;
    }


}