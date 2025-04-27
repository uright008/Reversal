package cn.stars.reversal.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("all")
public class ReversalLogger {
    // Enhanced Logger
    public static final Logger logger = LogManager.getLogger("Reversal");
    public static final String prefix = "[Reversal] ";

    public static void info(String s) {
        logger.info(prefix + s);
    }

    public static void info(String s, Object... o) {
        logger.info(prefix + s, o);
    }

    public static void warn(String s) {
        logger.warn(prefix + "{}", s);
    }

    public static void warn(String s, Object... o) {
        logger.warn(prefix + s, o);
    }

    public static void warn(String s, Throwable t) {
        logger.warn(prefix + s, t);
    }

    public static void error(String s) {
        logger.error(prefix + "{}", s);
    }

    public static void error(String s, Object... o) {
        logger.error(prefix + s, o);
    }

    public static void error(String s, Throwable t) {
        logger.error(prefix + s, t);
    }

    public static void debug(String s) {
        logger.debug(prefix + "{}", s);
    }

    public static void debug(String s, Object... o) {
        logger.debug(prefix + s, o);
    }

    public static void debug(String s, Throwable t) {
        logger.debug(prefix + s, t);
    }

    public static void fatal(String s) {
        logger.fatal(prefix + "{}", s);
    }

    public static void fatal(String s, Object... o) {
        logger.fatal(prefix + s, o);
    }

    public static void fatal(String s, Throwable t) {
        logger.fatal(prefix + s, t);
    }
}
