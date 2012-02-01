package com.bla.laa;

import com.bla.laa.Common.CommonS;
import org.slf4j.LoggerFactory;

public class ThreadRunner {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ThreadRunner.class);
    private static final String dontCountThreadNames[] = {"AWT"};

    ThreadRunner() {
        ThreadGroup tg = null;
        try {
            tg = new ThreadGroup("MainThreadGroup");
            for (; ; ) {
                if (getThreadCount(tg) < CommonS.threadMaxCount)
                    new Thread(tg, new CsddBkat()).start();

                Thread.currentThread().sleep(CommonS.threadDelay);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Global fucktup !!! (main thread termination)", e);
        }
    }

    private int getThreadCount(ThreadGroup tg) {
        int count = 0;
        try {
            Thread[] threads = new Thread[tg.activeCount()];
            tg.enumerate(threads, true);

            for (Thread thread : threads) {
                boolean found = false;
                for (String name : dontCountThreadNames) {
                    String threadName = thread.getName();
                    if ((!threadName.isEmpty()) && (thread.getName().contains(name)))
                        found = true;

                    if (threadName.isEmpty())
                        logger.error("");
                }
                if (!found)
                    count++;
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        //tg.list();
        //logger.debug("Thread count : "+ count);

        return count;

    }
}
