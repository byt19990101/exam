package com.zzxx.exam;

import com.zzxx.exam.contrller.ClientContext;
import com.zzxx.exam.entity.EntityContext;
import com.zzxx.exam.service.ExamService;
import com.zzxx.exam.ui.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        LoginFrame loginFrame = new LoginFrame();
        MenuFrame menuFrame = new MenuFrame();
        ExamFrame examFrame = new ExamFrame();
        MsgFrame msgFrame = new MsgFrame();
        WelcomeWindow welcomeWindow = new WelcomeWindow();

        ClientContext controller = new ClientContext();
        ExamService service = new ExamService();
        EntityContext entityContext = new EntityContext();

        //注入依赖
        controller.setLoginFrame(loginFrame);
        controller.setMenuFrame(menuFrame);
        controller.setExamFrame(examFrame);
        controller.setMsgFrame(msgFrame);
        controller.setWelcomeWindow(welcomeWindow);


        loginFrame.setController(controller);
        menuFrame.setController(controller);
        examFrame.setController(controller);
        controller.setService(service);
        service.setEntityContext(entityContext);


        controller.startShow();
    }
}
