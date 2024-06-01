//package com.project.chatfiabe.global.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOriginPatterns("https://www.chatfia.online")
//                .allowedOriginPatterns("https://chatfia.online")
//                .allowedOriginPatterns("https://api.chatfia.online")
//                .allowedOriginPatterns("http://www.chatfia.online")
//                .allowedOriginPatterns("http://chatfia.online")
//                .allowedOriginPatterns("http://api.chatfia.online")
//                .allowedOriginPatterns("https://chatfia.vercel.app")
//                .allowedOriginPatterns("http://chatfia.vercel.app")
//                .allowedOriginPatterns("http:localhost:3000")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
//                .allowCredentials(true)
//                .maxAge(3000);
//    }
//}
