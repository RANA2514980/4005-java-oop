package utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LoggerUtil {
    private static boolean configured = false;

    private LoggerUtil() {
    }

    public static Logger getLogger(Class<?> type) {
        configureOnce();
        return Logger.getLogger(type.getName());
    }

    private static synchronized void configureOnce() {
        if (configured) {
            return;
        }
        Logger rootLogger = Logger.getLogger("");
        for (var handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter());
        rootLogger.addHandler(handler);
        rootLogger.setLevel(Level.INFO);
        configured = true;
    }
}
