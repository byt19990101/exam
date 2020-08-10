package com.zzxx.exam.contrller;

import com.zzxx.exam.entity.ExamInfo;
import com.zzxx.exam.entity.QuestionInfo;
import com.zzxx.exam.entity.User;
import com.zzxx.exam.service.ExamService;
import com.zzxx.exam.service.IdOrPwdException;
import com.zzxx.exam.ui.*;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class ClientContext {
    private LoginFrame loginFrame;
    private MenuFrame menuFrame;
    private WelcomeWindow welcomeWindow;
    private ExamFrame examFrame;
    private MsgFrame msgFrame;
    private ExamService service;

    List<Integer> userAnswers = new ArrayList<>();

    //程序开始
    public void startShow() {
        welcomeWindow.setVisible(true);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        welcomeWindow.setVisible(false);
        loginFrame.setVisible(true);
    }

    private User user; //记录登录的用户

    //登录
    public void login() {
        // loginFrame 中获得 账号输入框 和密码输入框
        String id = loginFrame.getIdField().getText();
        String pwd = loginFrame.getPwField().getText();

        try {
            user = service.login(id, pwd);
            //界面跳转
            menuFrame.updateView(user);
            loginFrame.setVisible(false);
            menuFrame.setVisible(true);
        } catch (IdOrPwdException e) {
            loginFrame.updateMessage(e.getMessage());
        }

    }

    /*
        控制器开始考试的方法
     */
    private ExamInfo info;

    public void start() throws UnsupportedEncodingException {

        // 1.界面 菜单界面隐藏，考试界面-显示
        // 2.生成考试信息，以及试卷 -> List<Question>
        // ExanInfo -> 业务模块生成的
        // 试卷中的一道题目 -> 第一题
        // 访问业务层开始考试
        info = service.startExam(user);
        // 取得第一道题，用于显示考题
        currentQuestionInfo = service.getQuestionFormPaper(0);
        // 3. 更新考试界面
        examFrame.updateView(info, currentQuestionInfo);

        // 关闭菜单界面，打开考试界面
        menuFrame.setVisible(false);
        examFrame.setVisible(true);
        //examFrame.updateTime(1, 0, 0);

        Runnable r = () -> {
            long startTime = System.currentTimeMillis();
            while (addTime(startTime)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r).start();//运行时限倒计时
    }

    //返回true，继续计时；返回false，考试时间结束
    private boolean addTime(long startTime){
        long midTime = 60 * 60 - (System.currentTimeMillis() - startTime) / 1000;
        long hh = midTime / 60 / 60 % 60;
        long mm = midTime / 60 % 60;
        long ss = midTime % 60;
        examFrame.updateTime(hh, mm, ss);
        return midTime > 0;
    }

    //记录正在作答的题目信息
    private QuestionInfo currentQuestionInfo;
    private int questionIndex = 0;

    public void next() {
        if (questionIndex != info.getQuestionCount() - 1) {
            questionIndex++;
            currentQuestionInfo = service.getQuestionFormPaper(questionIndex);
            // 1.更新界面
            examFrame.updateView(info, currentQuestionInfo);
            // 2.记录当前这道题的用户答案
            userAnswers = examFrame.getUserAnswers();
            //currentQuestionInfo.setUserAnswers(userAnswers);
            service.getPaper().get(questionIndex).setUserAnswers(userAnswers);
        }
    }

    public void prev() {
        if (questionIndex != 0) {
            questionIndex--;
            currentQuestionInfo = service.getQuestionFormPaper(questionIndex);
            // 1.更新界面
            examFrame.updateView(info, currentQuestionInfo);
            // 2.记录当前这道题的用户答案
            userAnswers = examFrame.getUserAnswers();
            //currentQuestionInfo.setUserAnswers(userAnswers);
            service.getPaper().get(questionIndex).setUserAnswers(userAnswers);
        }
    }

    public void send() {
        System.out.println(service.calculateScore());
        JOptionPane.showMessageDialog(null, "提交成功！\n成绩为：" + service.calculateScore() + "分");
        examFrame.setVisible(false);
        menuFrame.setVisible(true);
    }

    //弹出查询分数页面
    public void resultStart() {
        JOptionPane.showMessageDialog(null, service.getScoreMessage());
    }

    //进入考试规则页面
    public void msgStart() {
        msgFrame.showMsg(service.msgStart());
        msgFrame.setVisible(true);
    }


    public void setLoginFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
    }

    public void setMenuFrame(MenuFrame menuFrame) {
        this.menuFrame = menuFrame;
    }

    public LoginFrame getLoginFrame() {
        return loginFrame;
    }

    public void setWelcomeWindow(WelcomeWindow welcomeWindow) {
        this.welcomeWindow = welcomeWindow;
    }

    public void setExamFrame(ExamFrame examFrame) {
        this.examFrame = examFrame;
    }

    public void setMsgFrame(MsgFrame msgFrame) {
        this.msgFrame = msgFrame;
    }

    public void setService(ExamService service) {
        this.service = service;
    }

}
