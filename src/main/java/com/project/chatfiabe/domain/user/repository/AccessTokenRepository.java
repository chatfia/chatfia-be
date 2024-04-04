package com.project.chatfiabe.domain.user.repository;

import com.project.chatfiabe.domain.user.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
    Optional<AccessToken> findByUserId(Long id);
}
