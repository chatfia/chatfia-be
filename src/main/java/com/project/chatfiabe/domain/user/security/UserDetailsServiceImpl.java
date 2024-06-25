package com.project.chatfiabe.domain.user.security;

import com.project.chatfiabe.domain.user.entity.User;
import com.project.chatfiabe.domain.user.repository.UserRepository;
import com.project.chatfiabe.global.exception.BaseException;
import com.project.chatfiabe.global.exception.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // 이메일 기반 유저 확인
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));
        return new UserDetailsImpl(user);
    }
}