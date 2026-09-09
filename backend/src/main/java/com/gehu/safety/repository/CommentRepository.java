package com.gehu.safety.repository;

import com.gehu.safety.entity.Comment;
import com.gehu.safety.entity.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostIdAndModerationStatus(Long postId, ModerationStatus status, Pageable pageable);
    long countByPostIdAndModerationStatus(Long postId, ModerationStatus status);
}
