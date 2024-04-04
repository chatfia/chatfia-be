package com.project.chatfiabe.global.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uuid = UUID.randomUUID().toString();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        log.info("Request: [{}][{}][{}]", uuid, method, uri);
        LogUtils.setLogInfo(uuid, method, uri);

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("Response: {} [{}]{{}]", response.getStatus(), LogUtils.getUuid(), handler);
        LogUtils.removeLog();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
