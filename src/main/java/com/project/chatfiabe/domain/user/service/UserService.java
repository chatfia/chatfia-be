package com.project.chatfiabe.domain.user.service;

import com.project.chatfiabe.domain.user.dto.SignupRequestDto;
import com.project.chatfiabe.domain.user.dto.SignupResponseDto;
import com.project.chatfiabe.domain.user.entity.User;
import com.project.chatfiabe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponseDto signup(SignupRequestDto requestDto) {
        Optional<User> userByEmail = userRepository.findByEmail(requestDto.getEmail());
        if (userByEmail.isPresent()) {
            throw new RuntimeException(requestDto.getEmail() + "already exists");
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
}
