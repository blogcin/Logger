package xyz.miffle.logger;

import cn.nukkit.plugin.PluginLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by blogcin on 23/11/16.
 */
public class Logger {

    private static Log logger;

    public Logger(PluginLogger logger) {
        Logger.logger = new Log(logger);
        Logger.logger.start();
    }

    public static void info(String message) {
        logger.addInfo(message);
    }

    private class Log extends Thread {
        private PluginLogger pluginLogger = null;
        private HashMap<LogLevel, String> messages = null;
        private final Lock mutex = new ReentrantLock(true);

        public Log(PluginLogger pluginLogger) {
            this.pluginLogger = pluginLogger;
            messages = new HashMap<>();
        }

        public void addInfo(String message) {
            if (messages != null && message != null) {
                mutex.lock();
                messages.put(LogLevel.INFO, message);
                mutex.unlock();
            }
        }

        @Override
        public void run() {
            ArrayList<String> infoMessages = new ArrayList<>();
            Iterator<LogLevel> iterator = null;
            LogLevel logLevel;

            while(true) {
                mutex.lock();

                iterator = messages.keySet().iterator();

                while(iterator.hasNext()) {
                    logLevel = iterator.next();

                    switch(logLevel) {
                        case INFO:
                            infoMessages.add(messages.get(logLevel));
                            break;
                    }
                }
                messages.clear();
                mutex.unlock();

                for(String message : infoMessages) {
                    pluginLogger.info(message);
                }

                infoMessages.clear();
            }

        }
    }
}
