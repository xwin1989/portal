package com.nx.config.web;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by neal.xu on 2014/10/15.
 */
public class SessionListener implements HttpSessionListener {

    public static AtomicInteger ONLINE = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("Session Create");
        se.getSession().setMaxInactiveInterval(5 * 60);
        ONLINE.getAndDecrement();
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("Session Destroy");
        ONLINE.getAndDecrement();
    }
}
