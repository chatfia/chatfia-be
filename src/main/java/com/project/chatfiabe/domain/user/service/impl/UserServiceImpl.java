package com.project.chatfiabe.domain.user.service.impl;

import com.project.chatfiabe.domain.user.dto.*;
import com.project.chatfiabe.domain.user.entity.User;
import com.project.chatfiabe.domain.user.repository.UserRepository;
import com.project.chatfiabe.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponseDto signup(SignupRequestDto requestDto) {
        Optional<User> userByEmail = userRepository.findByEmail(requestDto.getEmail());
        if (userByEmail.isPresent()) {
            throw new RuntimeException(requestDto.getEmail() + "already exists");
        }

        if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        User createUser = new User(
                requestDto.getEmail(),
                passwordEncoder.encode(requestDto.getPassword()),
                requestDto.getNickname()
        );
        userRepository.save(createUser);

        return new SignupResponseDto(
                createUser.getId(),
                createUser.getEmail(),
                createUser.getPassword(),
                createUser.getNickname()
        );
    }

    // 닉네임 수정
    public UserInfoResponseDto updateUserInfo(User user, String newNickname) {
        user.updateNickname(newNickname);
        return new UserInfoResponseDto(user);
    }

    // 회원탈퇴
    public void deleteUser(User user, DeleteUserInfoRequestDto requestDto) {
        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("기존의 비밀번호가 일치하지 않습니다.");
        }

        userRepository.delete(user);
    }

    // 비밀번호 변경
    public void updatePassword(User user, UserInfoRequestDto requestDto) {
        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("기존의 비밀번호가 일치하지 않습니다.");
        }

        if (requestDto.getNewPassword() != null) {
            throw new NullPointerException("새로운 비밀번호가 입력되지 않았습니다.");
        }

        if (!requestDto.getNewPassword().equals(requestDto.getNewPasswordConfirm())) {
            throw new IllegalArgumentException("새 비밀번호 확인이 틀렸습니다.");
        }

        user.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }

    // 회원정보 조회
    public UserInfoResponseDto getUserInfo(User user) {

        return new UserInfoResponseDto(user.getId(), user.getEmail(), user.getNickname());
    }
}
