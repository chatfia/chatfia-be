package com.project.chatfiabe.global.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

public class HttpRequestUtils {
    public static String getUserAgent(HttpServletRequest request){
        return Objects.nonNull(request.getHeader("user-agent")) ? request.getHeader("user-agent") : "";
    }

    public static String getRemoteAddr(HttpServletRequest request){
        return Objects.nonNull(request.getHeader("X-FORWARDED-FOR")) ? request.getHeader("X-FORWARDED-FOR") : request.getRemoteAddr();
    }
}
