package com.gehu.safety.service.impl;

import com.gehu.safety.dto.request.CreatePostRequest;
import com.gehu.safety.dto.response.PageResponse;
import com.gehu.safety.dto.response.PostDetailResponse;
import com.gehu.safety.dto.response.PostSummaryResponse;
import com.gehu.safety.entity.*;
import com.gehu.safety.exception.ResourceNotFoundException;
import com.gehu.safety.repository.CommunityPostRepository;
import com.gehu.safety.repository.ModerationQueueRepository;
import com.gehu.safety.repository.UserRepository;
import com.gehu.safety.service.CommunityPostService;
import com.gehu.safety.service.ContentModerationChecker;
import com.gehu.safety.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityPostServiceImpl implements CommunityPostService {

    private static final int EXCERPT_LENGTH = 200;

    private final CommunityPostRepository postRepository;
    private final UserRepository userRepository;
    private final ModerationQueueRepository moderationQueueRepository;
    private final ContentModerationChecker moderationChecker;
    private final VoteService voteService;

    @Override
    @Transactional
    public PostDetailResponse createPost(Long authorUserId, CreatePostRequest request) {
        User author = userRepository.findById(authorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String flagReason = moderationChecker.checkContent(request.getTitle() + " " + request.getBody());
        ModerationStatus status = (flagReason != null) ? ModerationStatus.PENDING_REVIEW : ModerationStatus.PUBLISHED;

        CommunityPost post = CommunityPost.builder()
                .author(author)                                   // internal FK only, never mapped to a DTO
                .anonymousHandleSnapshot(author.getAnonymousHandle()) // what everyone else actually sees
                .title(request.getTitle())
                .body(request.getBody())
                .category(request.getCategory())
                .areaLabel(request.getAreaLabel())
                .moderationStatus(status)
                .build();

        post = postRepository.save(post);

        if (flagReason != null) {
            ModerationQueueItem queueItem = ModerationQueueItem.builder()
                    .targetType(TargetType.POST)
                    .targetId(post.getId())
                    .reason(flagReason)
                    .flaggedBy(ReportSource.AI)
                    .status(ModerationQueueStatus.PENDING)
                    .build();
            moderationQueueRepository.save(queueItem);
        }

        return toDetailResponse(post, authorUserId);
    }

    @Override
    public PageResponse<PostSummaryResponse> getFeed(Long callerUserId, PostCategory category,
                                                       String keyword, String sort, int page, int size) {
        Sort sortOrder = "top".equalsIgnoreCase(sort)
                ? Sort.by(Sort.Direction.DESC, "upvoteCount")
                : Sort.by(Sort.Direction.DESC, "createdAt");

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<CommunityPost> results = postRepository.search(
                ModerationStatus.PUBLISHED,
                category,
                (keyword != null && !keyword.isBlank()) ? keyword.trim() : null,
                pageable
        );

        Page<PostSummaryResponse> mapped = results.map(post -> toSummaryResponse(post, callerUserId));
        return PageResponse.from(mapped);
    }

    @Override
    public PostDetailResponse getPostDetail(Long callerUserId, Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // Regular users should never be able to view a removed/pending post by
        // guessing its ID. Only the author or a moderator/admin can (moderation
        // controller uses a separate path, so a plain 404 here is correct).
        if (post.getModerationStatus() != ModerationStatus.PUBLISHED
                && !post.getAuthor().getId().equals(callerUserId)) {
            throw new ResourceNotFoundException("Post not found");
        }

        return toDetailResponse(post, callerUserId);
    }

    private PostSummaryResponse toSummaryResponse(CommunityPost post, Long callerUserId) {
        String body = post.getBody();
        String excerpt = body.length() > EXCERPT_LENGTH ? body.substring(0, EXCERPT_LENGTH) + "..." : body;

        return PostSummaryResponse.builder()
                .id(post.getId())
                .anonymousHandle(post.getAnonymousHandleSnapshot())
                .title(post.getTitle())
                .excerpt(excerpt)
                .category(post.getCategory())
                .areaLabel(post.getAreaLabel())
                .upvoteCount(post.getUpvoteCount())
                .downvoteCount(post.getDownvoteCount())
                .commentCount(post.getCommentCount())
                .myVote(voteService.getMyVote(callerUserId, TargetType.POST, post.getId()))
                .createdAt(post.getCreatedAt())
                .build();
    }

    private PostDetailResponse toDetailResponse(CommunityPost post, Long callerUserId) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .anonymousHandle(post.getAnonymousHandleSnapshot())
                .title(post.getTitle())
                .body(post.getBody())
                .category(post.getCategory())
                .areaLabel(post.getAreaLabel())
                .upvoteCount(post.getUpvoteCount())
                .downvoteCount(post.getDownvoteCount())
                .commentCount(post.getCommentCount())
                .myVote(voteService.getMyVote(callerUserId, TargetType.POST, post.getId()))
                .createdAt(post.getCreatedAt())
                .build();
    }
}
