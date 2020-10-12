package com.cubic.viedo.session;


import com.cubic.viedo.webscoket.WebConnection;

/**
 * @ClassName SessionManager
 * @Author QIANGLU
 * @Date 2020/4/22 2:45 下午
 * @Version 1.0
 */
public interface SessionManager {

    Session create(WebConnection webConnection);

    Session getSession(String id);
}
