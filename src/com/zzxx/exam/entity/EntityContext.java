package com.zzxx.exam.entity;

import com.zzxx.exam.util.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实体数据管理, 用来读取数据文件放到内存集合当中
 */
public class EntityContext {
    //key - 用户的编号id，value - 用户对象
    private Map<String, User> users = new HashMap<>();
    //key - 试题的难度级别，value - 该难度级别对应的所有试题
    private Map<Integer, List<Question>> questions = new HashMap<>();
    //msg - 存储考试规则
    private StringBuilder msg = new StringBuilder("");


    public EntityContext() throws IOException {
        Config config = new Config("config.properties");
        loadUsers(config.getString("UserFile"));
        loadQuestions(config.getString("QuestionFile"));
        loadMsg(config.getString("ExamRule"));
    }

    /**
     * 读取user.txt文件，将其中的数据，封装为用户对象，然后存储到集合当中
     *
     * @param filename
     */
    public void loadUsers(String filename) throws IOException {
        File file = new File("src/com/zzxx/exam/util/" + filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String us;
        while ((us = br.readLine()) != null) {
            if (us.startsWith("#") || us.equals("")) {
                continue;
            }
            //System.out.println(us);
            String[] inf = us.split(":");
            String id = inf[0];
            String name = inf[1];
            String password = inf[2];
            User user = new User(name, Integer.parseInt(id), password);
            user.setPhone(inf[3]);
            user.setEmail(inf[4]);
            users.put(id, user);
        }
        //System.out.println(users.toString());
    }

    /**
     * 读取corejava.txt文件，将其中的数据封装为Question对象，然后存储到集合当中
     *
     * @param filename
     */
    public void loadQuestions(String filename) throws IOException {
        File file = new File("src/com/zzxx/exam/util/" + filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        //列表que表示所有题目的集合
        List<Question> que = new ArrayList<>();
        String us;
        while ((us = br.readLine()) != null) {
            if (us.startsWith("@")) {
                us = us.replace("@", "");
                String[] str = us.split(",");
                int score = Integer.parseInt(str[1].split("=")[1]);
                int level = Integer.parseInt(str[2].split("=")[1]);
                List<Integer> answers = new ArrayList<>();
                int al = str[0].split("=")[1].split("/").length;
                for (int i = 0; i < al; i++) {
                    answers.add(Integer.parseInt(str[0].split("=")[1].split("/")[i]));
                }
                //System.out.println(us);
                String title = br.readLine();
                List<String> options = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    options.add(br.readLine());
                }
                Question q = new Question();
                q.setLevel(level);
                q.setScore(score);
                q.setAnswers(answers);
                q.setTitle(title);
                q.setOptions(options);
                if (al == 1) {
                    q.setType(Question.SINGLE_SELECTION);
                } else {
                    q.setType(Question.MULTI_SELECTION);
                }
                que.add(q);
                //questions.put(level, que);

                List<Question> list = this.questions.get(level);
                if (list == null) {
                    list = new ArrayList<>();
                    questions.put(level, list);
                }
                list.add(q);
            }
        }

    }

    //读取考试规则rule.txt存储进msg
    public void loadMsg(String filename) throws IOException {
        File rule = new File("src/com/zzxx/exam/util/" + filename);
        FileReader fr = new FileReader(rule);
        int len;
        char[] chars = new char[1024];
        while ((len = fr.read(chars)) != -1) {
            msg.append(chars, 0, len);
        }

    }

    public Map<String, User> getUsers() {
        return users;
    }

    public String getMsg() {
        return msg.toString();
    }

    public Map<Integer, List<Question>> getQuestions() {
        return questions;
    }

    //根据用户id，从数据库中查询对象
    public User findUserById(Integer id) {
        return users.get(String.valueOf(id));
    }

    //根据试题的难度级别，获得对应难度级别的试题列表
    public List<Question> findQuestionsByLevel(int level) {
        return questions.get(level);
    }
}
