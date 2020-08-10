package com.zzxx.exam.service;

import com.zzxx.exam.entity.*;
import com.zzxx.exam.util.Config;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class ExamService {

    private EntityContext entityContext;

    private ExamInfo info = new ExamInfo();

    //登录
    public User login(String id, String password) throws IdOrPwdException {
        //在这里写登录的过程
        //1.获得用户输入的账号，密码 （构造方法中已经调用过）
        //2.在模拟数据库中的users 查找有没有对应的user对象
        User user = entityContext.findUserById(Integer.valueOf(id));
        //3.如果有user，密码正确，登录成功，界面跳转
        if (user != null) {
            //判断密码
            if (password.equals(user.getPassword())) {
                return user;
            }
        }
        //4.如果有user，密码不正确，提示信息
        //5.没有user，提示信息
        throw new IdOrPwdException("编号/密码错误");
    }

    public ExamInfo startExam(User user) throws UnsupportedEncodingException {
        Config config = new Config("config.properties");
        info.setTimeLimit(config.getInt("TimeLimit"));
        info.setQuestionCount(config.getInt("QuestionNumber"));

        String paperTitle = new String(config.getString("PaperTitle").getBytes("ISO8859-1"), StandardCharsets.UTF_8);
        info.setTitle(paperTitle);
        info.setUser(user);

        //生成一套试卷
        createExamPaper();
        return info;
    }

    //定义一套试卷
    private List<QuestionInfo> paper = new ArrayList<>();

    public List<QuestionInfo> getPaper() {
        return paper;
    }

    private List<String> paperAnswers = new ArrayList<>();
    private List<Integer> paperScore = new ArrayList<>();

    /**
     * 创建考卷
     * 规则：每个难度级别两道题
     */
    private void createExamPaper() {
        Random r = new Random();
        int index = 0;
        for (int level = Question.LEVEL1; level <= Question.LEVEL10; level++) {
            //获得难度级别对应的所有试题
            List<Question> list = entityContext.findQuestionsByLevel(level);
            //随机获得两个试题对象，并且加入到paper中
            //从list中取出（remove）一道题
            Question q1 = list.remove(r.nextInt(list.size()));
            Question q2 = list.remove(r.nextInt(list.size()));
            paperAnswers.add(Arrays.toString(new List[]{q1.getAnswers()}));
            paperScore.add(q1.getScore());
            paperAnswers.add(Arrays.toString(new List[]{q2.getAnswers()}));
            paperScore.add(q2.getScore());
            paper.add(new QuestionInfo(index++, q1));
            paper.add(new QuestionInfo(index++, q2));

        }
    }

    //考试计时器
    public long[] timer(){
        long startTime = System.currentTimeMillis();
        long time = System.currentTimeMillis();
        long midTime = info.getTimeLimit() * 60 * 60 - (time - startTime) / 1000;
        long hh = midTime / 60 / 60 % 60;
        long mm = midTime / 60 % 60;
        long ss = midTime % 60;
        return new long[]{hh, mm, ss};

    }

    //计算分数
    public int calculateScore() {
        int score = 0;
        for (int i = 0; i < paper.size(); i++) {
            String str = Arrays.toString(new List[]{paper.get(i).getUserAnswers()});
            if (str.equals(paperAnswers.get(i))) {
                score = score + paperScore.get(i);
            }
        }
        return score;
    }

    public String getScoreMessage() {
        try {
            String str = info.getUser().getName() + " 同学在" + info.getTitle() + " 科目上的成绩为：\n" + calculateScore() + "分";
            return str;
        } catch (NullPointerException e) {
            return "没有考试信息！";
        }
    }

    public QuestionInfo getQuestionFormPaper(int i) {
        return paper.get(i);
    }

    //返回考试规则字符串
    public String msgStart() {
        return entityContext.getMsg();
    }

    public void setEntityContext(EntityContext entityContext) {
        this.entityContext = entityContext;
    }
}
