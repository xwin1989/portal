package com.nx.config.web;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Neal on 10/15 015.
 */
public class WebSessionManager extends DefaultWebSessionManager {

    public static AtomicInteger ONLINE = new AtomicInteger(0);

    @Override
    protected void onStart(Session session, SessionContext context) {
        super.onStart(session, context);
        ONLINE.getAndIncrement();
        System.out.println("Session Create");
    }

    @Override
    protected void onStop(Session session) {
        super.onStop(session);
        ONLINE.getAndDecrement();
        System.out.println("Session Stop");
    }
}
