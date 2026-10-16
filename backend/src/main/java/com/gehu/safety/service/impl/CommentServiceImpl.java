package com.gehu.safety.service.impl;

import com.gehu.safety.ai.AiTextAnalysisService;
import com.gehu.safety.ai.dto.ModerationResult;
import com.gehu.safety.dto.request.CreateCommentRequest;
import com.gehu.safety.dto.response.CommentResponse;
import com.gehu.safety.dto.response.PageResponse;
import com.gehu.safety.entity.*;
import com.gehu.safety.exception.ResourceNotFoundException;
import com.gehu.safety.repository.CommentRepository;
import com.gehu.safety.repository.CommunityPostRepository;
import com.gehu.safety.repository.ModerationQueueRepository;
import com.gehu.safety.repository.UserRepository;
import com.gehu.safety.service.CommentService;
import com.gehu.safety.service.VoteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommunityPostRepository postRepository;
    private final UserRepository userRepository;
    private final ModerationQueueRepository moderationQueueRepository;
    private final AiTextAnalysisService aiTextAnalysisService;
    private final VoteService voteService;


    public CommentServiceImpl(CommentRepository commentRepository,
            CommunityPostRepository postRepository,
            UserRepository userRepository,
            ModerationQueueRepository moderationQueueRepository,
            AiTextAnalysisService aiTextAnalysisService,
            VoteService voteService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.moderationQueueRepository = moderationQueueRepository;
        this.aiTextAnalysisService = aiTextAnalysisService;
        this.voteService = voteService;
    }
    @Override
    @Transactional
    public CommentResponse addComment(Long authorUserId, Long postId, CreateCommentRequest request) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (post.getModerationStatus() != ModerationStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Post not found");
        }

        User author = userRepository.findById(authorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ModerationResult moderation = aiTextAnalysisService.analyzeForModeration(request.getBody(), authorUserId);
        ModerationStatus status = moderation.shouldQueue() ? ModerationStatus.PENDING_REVIEW : ModerationStatus.PUBLISHED;

        Comment comment = Comment.builder()
                .post(post)
                .author(author)                                     // internal FK only
                .anonymousHandleSnapshot(author.getAnonymousHandle())
                .body(request.getBody())
                .moderationStatus(status)
                .build();

        comment = commentRepository.save(comment);

        if (moderation.shouldQueue()) {
            ModerationQueueItem queueItem = ModerationQueueItem.builder()
                    .targetType(TargetType.COMMENT)
                    .targetId(comment.getId())
                    .reason(moderation.getReasoning())
                    .flaggedBy(ReportSource.AI)
                    .status(ModerationQueueStatus.PENDING)
                    .build();
            moderationQueueRepository.save(queueItem);
        } else {
            // Only bump the visible comment count for content that is actually published.
            post.setCommentCount(post.getCommentCount() + 1);
            postRepository.save(post);
        }

        return toResponse(comment, authorUserId);
    }

    @Override
    public PageResponse<CommentResponse> getComments(Long callerUserId, Long postId, int page, int size) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<Comment> comments = commentRepository.findByPostIdAndModerationStatus(postId, ModerationStatus.PUBLISHED, pageable);

        Page<CommentResponse> mapped = comments.map(c -> toResponse(c, callerUserId));
        return PageResponse.from(mapped);
    }

    private CommentResponse toResponse(Comment comment, Long callerUserId) {
        return CommentResponse.builder()
                .id(comment.getId())
                .anonymousHandle(comment.getAnonymousHandleSnapshot())
                .body(comment.getBody())
                .upvoteCount(comment.getUpvoteCount())
                .downvoteCount(comment.getDownvoteCount())
                .myVote(voteService.getMyVote(callerUserId, TargetType.COMMENT, comment.getId()))
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
