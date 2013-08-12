package com.alibaba.otter.shared.common.utils.thread;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 多线程TreahdFactory工厂，允许指定线程名字
 * 
 * @author jianghang 2011-9-19 下午09:00:37
 * @version 4.0.0
 */
public class NamedThreadFactory implements ThreadFactory {

    private static final Logger           logger                   = LoggerFactory.getLogger(NamedThreadFactory.class);
    final private static String           DEFAULT_NAME             = "otter-pool";
    final private String                  name;
    final private boolean                 daemon;
    final private ThreadGroup             group;
    final private AtomicInteger           threadNumber             = new AtomicInteger(0);
    final static UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {

                                                                       public void uncaughtException(Thread t,
                                                                                                     Throwable e) {
                                                                           logger.error("from " + t.getName(), e);
                                                                       }
                                                                   };

    public NamedThreadFactory(){
        this(DEFAULT_NAME, true);
    }

    public NamedThreadFactory(String name){
        this(name, true);
    }

    public NamedThreadFactory(String name, boolean daemon){
        this.name = name;
        this.daemon = daemon;
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, name + "-" + threadNumber.getAndIncrement(), 0);
        t.setDaemon(daemon);
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }

        t.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        return t;
    }

}
