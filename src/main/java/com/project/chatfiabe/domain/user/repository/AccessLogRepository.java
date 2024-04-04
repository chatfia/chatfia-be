package com.project.chatfiabe.domain.user.repository;

import com.project.chatfiabe.domain.user.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
}
