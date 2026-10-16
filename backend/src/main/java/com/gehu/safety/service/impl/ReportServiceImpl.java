package com.gehu.safety.service.impl;

import com.gehu.safety.dto.request.ReportContentRequest;
import com.gehu.safety.entity.*;
import com.gehu.safety.exception.BadRequestException;
import com.gehu.safety.exception.ResourceNotFoundException;
import com.gehu.safety.repository.*;
import com.gehu.safety.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportServiceImpl implements ReportService {

    private final ModerationQueueRepository moderationQueueRepository;
    private final CommunityPostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;


    public ReportServiceImpl(ModerationQueueRepository moderationQueueRepository,
            CommunityPostRepository postRepository,
            CommentRepository commentRepository,
            UserRepository userRepository) {
        this.moderationQueueRepository = moderationQueueRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }
    @Override
    @Transactional
    public void reportContent(Long reporterUserId, ReportContentRequest request) {
        boolean targetExists = (request.getTargetType() == TargetType.POST)
                ? postRepository.existsById(request.getTargetId())
                : commentRepository.existsById(request.getTargetId());

        if (!targetExists) {
            throw new ResourceNotFoundException(request.getTargetType() + " not found");
        }

        boolean alreadyReported = moderationQueueRepository.existsByTargetTypeAndTargetIdAndReporterUserId(
                request.getTargetType(), request.getTargetId(), reporterUserId);
        if (alreadyReported) {
            throw new BadRequestException("You have already reported this content");
        }

        User reporter = userRepository.findById(reporterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ModerationQueueItem item = ModerationQueueItem.builder()
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .reason(request.getReason())
                .flaggedBy(ReportSource.USER)
                .reporterUser(reporter) // internal only, never exposed - see ModerationQueueItem javadoc
                .status(ModerationQueueStatus.PENDING)
                .build();

        moderationQueueRepository.save(item);
    }
}
