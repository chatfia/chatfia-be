package com.project.chatfiabe.global.logging;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class LogUtils {
    private static  final ThreadLocal<LogInfo> LOG_INFO = new ThreadLocal<>();

    public static void setLogInfo(String uuid, String method, String uri) {
        LOG_INFO.set(new LogInfo(uuid, method, uri));
    }

    public static String getUuid() {
        return LOG_INFO.get().getUuid();
    }

    public static String getMethod() {
        return LOG_INFO.get().getMethod();
    }

    public static String getUri() {
        return LOG_INFO.get().getUri();
    }

    public static void removeLog() {
        LOG_INFO.remove();
    }

    @Getter
    @AllArgsConstructor
    public static class LogInfo {
        private String uuid;
        private String method;
        private String uri;
    }
}
