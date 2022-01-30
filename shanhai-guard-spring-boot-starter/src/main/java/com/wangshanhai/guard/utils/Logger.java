package com.wangshanhai.guard.utils;

import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 */
public class Logger {
    public Logger() {
    }

    public static void trace(String format, Object... arguments) {
        LoggerFactory.getLogger(getClassName()).trace(format, arguments);
    }

    public static void debug(String format, Object... arguments) {
        LoggerFactory.getLogger(getClassName()).debug(format, arguments);
    }

    public static void info(String format, Object... arguments) {
        LoggerFactory.getLogger(getClassName()).info(format, arguments);
    }

    public static void warn(String format, Object... arguments) {
        LoggerFactory.getLogger(getClassName()).warn(format, arguments);
    }

    public static void error(String format, Object... arguments) {
        LoggerFactory.getLogger(getClassName()).error(format, arguments);
    }

    /**
     * 获取调用 error,info,debug静态类的类名
     */
    private  static String getClassName() {
        return new SecurityManager() {
            public final String getClassName() {
                return getClassContext()[3].getName();
            }
        }.getClassName();
    }
}
