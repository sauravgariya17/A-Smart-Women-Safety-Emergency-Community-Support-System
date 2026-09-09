package com.gehu.safety.service;

import com.gehu.safety.dto.request.CreateCommentRequest;
import com.gehu.safety.dto.response.CommentResponse;
import com.gehu.safety.dto.response.PageResponse;

public interface CommentService {
    CommentResponse addComment(Long authorUserId, Long postId, CreateCommentRequest request);
    PageResponse<CommentResponse> getComments(Long callerUserId, Long postId, int page, int size);
}
