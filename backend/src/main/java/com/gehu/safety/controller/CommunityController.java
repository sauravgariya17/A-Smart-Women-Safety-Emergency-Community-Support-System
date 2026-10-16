package com.gehu.safety.controller;

import com.gehu.safety.dto.request.*;
import com.gehu.safety.dto.response.*;
import com.gehu.safety.entity.PostCategory;
import com.gehu.safety.entity.TargetType;
import com.gehu.safety.security.UserPrincipal;
import com.gehu.safety.service.CommentService;
import com.gehu.safety.service.CommunityPostService;
import com.gehu.safety.service.ReportService;
import com.gehu.safety.service.VoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * All endpoints require authentication (see SecurityConfig - anyRequest().authenticated()
 * covers everything under /api/community/**). The community is for verified
 * GEHU users only, not the open internet, which is itself a privacy control.
 */
@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityPostService postService;
    private final CommentService commentService;
    private final VoteService voteService;
    private final ReportService reportService;


    public CommunityController(CommunityPostService postService,
            CommentService commentService,
            VoteService voteService,
            ReportService reportService) {
        this.postService = postService;
        this.commentService = commentService;
        this.voteService = voteService;
        this.reportService = reportService;
    }
    // ---------- Posts ----------

    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<PostDetailResponse>> createPost(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreatePostRequest request) {

        PostDetailResponse response = postService.createPost(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Post created", response));
    }

    @GetMapping("/posts")
    public ApiResponse<PageResponse<PostSummaryResponse>> getFeed(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) PostCategory category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "new") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.success(
                postService.getFeed(principal.getId(), category, keyword, sort, page, size));
    }

    @GetMapping("/posts/{postId}")
    public ApiResponse<PostDetailResponse> getPostDetail(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long postId) {

        return ApiResponse.success(postService.getPostDetail(principal.getId(), postId));
    }

    // ---------- Comments ----------

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request) {

        CommentResponse response = commentService.addComment(principal.getId(), postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Comment added", response));
    }

    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<PageResponse<CommentResponse>> getComments(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ApiResponse.success(commentService.getComments(principal.getId(), postId, page, size));
    }

    // ---------- Votes ----------

    @PostMapping("/posts/{postId}/vote")
    public ApiResponse<Integer> voteOnPost(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long postId,
            @Valid @RequestBody VoteRequest request) {

        int result = voteService.castVote(principal.getId(), TargetType.POST, postId, request.getVoteValue());
        return ApiResponse.success("Vote recorded", result);
    }

    @PostMapping("/comments/{commentId}/vote")
    public ApiResponse<Integer> voteOnComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long commentId,
            @Valid @RequestBody VoteRequest request) {

        int result = voteService.castVote(principal.getId(), TargetType.COMMENT, commentId, request.getVoteValue());
        return ApiResponse.success("Vote recorded", result);
    }

    // ---------- Report ----------

    @PostMapping("/report")
    public ApiResponse<Void> reportContent(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ReportContentRequest request) {

        reportService.reportContent(principal.getId(), request);
        return ApiResponse.success("Content reported. Our moderators will review it.", null);
    }
}
