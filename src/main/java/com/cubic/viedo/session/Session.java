package com.cubic.viedo.session;


import com.cubic.viedo.webscoket.WebConnection;

/**
 * @ClassName Session
 * @Author QIANGLU
 * @Date 2020/4/22 2:46 下午
 * @Version 1.0
 */
public interface Session {

    enum State {
        finish, down
    }

    void writeToAgent(String data);

    void writeToWeb(String data);

    WebConnection getWebConnection();


    String getId();
}
