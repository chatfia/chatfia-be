package com.project.chatfiabe.domain.user.jwt.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chatfiabe.global.exception.BaseResponse;
import com.project.chatfiabe.global.exception.BaseResponseStatus;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class FilterResponseUtil {

    public static void sendFilterResponse(HttpServletResponse httpServletResponse,
                                          int statusCode,
                                          BaseResponseStatus baseResponseStatus) throws IOException {
        BaseResponse<Void> baseResponse = new BaseResponse<>(baseResponseStatus);
        httpServletResponse.setStatus(statusCode);
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(baseResponse));
    }
}