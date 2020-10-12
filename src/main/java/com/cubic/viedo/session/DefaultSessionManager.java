package com.cubic.viedo.session;

import com.google.common.collect.Maps;
import com.cubic.viedo.webscoket.WebConnection;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName DefaultSessionManager
 * @Author QIANGLU
 * @Date 2020/4/22 2:52 下午
 * @Version 1.0
 */
@Service
public class DefaultSessionManager implements SessionManager, Runnable {

    private final ConcurrentMap<String, Session> sessions = Maps.newConcurrentMap();

    private ScheduledFuture future = new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(this, 30, 30, TimeUnit.SECONDS);


    @Override
    public Session create(WebConnection webConnection) {
        String id = UUID.randomUUID().toString();
        Session session = new DefaultSession(id, webConnection);
        Session oldSession = sessions.putIfAbsent(id, session);
        if (oldSession != null) {
            return oldSession;
        }
        return session;
    }

    @Override
    public Session getSession(String id) {
        return sessions.get(id);
    }

    @Override
    public void run() {

        sessions.entrySet().removeIf(entry -> {
            Session session = entry.getValue();
            if (!session.getWebConnection().getChannel().isActive()) {
                session.getWebConnection().close();
                return true;
            }
            return false;
        });

    }
}
