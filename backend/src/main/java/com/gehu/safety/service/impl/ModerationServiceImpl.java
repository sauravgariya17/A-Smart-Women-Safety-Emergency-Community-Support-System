package com.gehu.safety.service.impl;

import com.gehu.safety.dto.request.ModerationReviewRequest;
import com.gehu.safety.dto.response.ModerationQueueItemResponse;
import com.gehu.safety.dto.response.PageResponse;
import com.gehu.safety.entity.*;
import com.gehu.safety.exception.ResourceNotFoundException;
import com.gehu.safety.repository.CommentRepository;
import com.gehu.safety.repository.CommunityPostRepository;
import com.gehu.safety.repository.ModerationQueueRepository;
import com.gehu.safety.repository.UserRepository;
import com.gehu.safety.service.ModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ModerationServiceImpl implements ModerationService {

    private static final int PREVIEW_LENGTH = 150;

    private final ModerationQueueRepository moderationQueueRepository;
    private final CommunityPostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    public PageResponse<ModerationQueueItemResponse> getPendingQueue(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<ModerationQueueItem> items = moderationQueueRepository.findByStatus(ModerationQueueStatus.PENDING, pageable);
        return PageResponse.from(items.map(this::toResponse));
    }

    @Override
    @Transactional
    public void reviewItem(Long moderatorUserId, Long queueId, ModerationReviewRequest request) {
        ModerationQueueItem item = moderationQueueRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue item not found"));

        User moderator = userRepository.findById(moderatorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        item.setStatus(request.getDecision());
        item.setReviewedBy(moderator);
        item.setReviewedAt(LocalDateTime.now());
        moderationQueueRepository.save(item);

        ModerationStatus newContentStatus = (request.getDecision() == ModerationQueueStatus.APPROVED)
                ? ModerationStatus.PUBLISHED
                : ModerationStatus.REMOVED;

        if (item.getTargetType() == TargetType.POST) {
            CommunityPost post = postRepository.findById(item.getTargetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
            post.setModerationStatus(newContentStatus);
            postRepository.save(post);
        } else {
            Comment comment = commentRepository.findById(item.getTargetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
            comment.setModerationStatus(newContentStatus);
            commentRepository.save(comment);

            // Keep the parent post's visible comment_count accurate when a
            // previously-published comment is retroactively removed.
            if (newContentStatus == ModerationStatus.REMOVED) {
                CommunityPost post = comment.getPost();
                post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
                postRepository.save(post);
            }
        }
    }

    private ModerationQueueItemResponse toResponse(ModerationQueueItem item) {
        String preview;
        String authorHandle;

        if (item.getTargetType() == TargetType.POST) {
            CommunityPost post = postRepository.findById(item.getTargetId()).orElse(null);
            preview = post != null ? truncate(post.getTitle() + " - " + post.getBody()) : "[content deleted]";
            authorHandle = post != null ? post.getAnonymousHandleSnapshot() : "unknown";
        } else {
            Comment comment = commentRepository.findById(item.getTargetId()).orElse(null);
            preview = comment != null ? truncate(comment.getBody()) : "[content deleted]";
            authorHandle = comment != null ? comment.getAnonymousHandleSnapshot() : "unknown";
        }

        // NOTE: authorHandle here is the anonymous handle only - moderators
        // still cannot see the real name behind the flagged content. Also
        // note reporterUser is intentionally never read here at all.
        return ModerationQueueItemResponse.builder()
                .queueId(item.getId())
                .targetType(item.getTargetType())
                .targetId(item.getTargetId())
                .reason(item.getReason())
                .flaggedBy(item.getFlaggedBy())
                .status(item.getStatus())
                .contentPreview(preview)
                .contentAuthorHandle(authorHandle)
                .createdAt(item.getCreatedAt())
                .build();
    }

    private String truncate(String text) {
        return text.length() > PREVIEW_LENGTH ? text.substring(0, PREVIEW_LENGTH) + "..." : text;
    }
}
