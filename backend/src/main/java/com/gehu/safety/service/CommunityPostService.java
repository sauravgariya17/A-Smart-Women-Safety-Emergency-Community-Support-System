package com.gehu.safety.service;

import com.gehu.safety.dto.request.CreatePostRequest;
import com.gehu.safety.dto.response.PageResponse;
import com.gehu.safety.dto.response.PostDetailResponse;
import com.gehu.safety.dto.response.PostSummaryResponse;
import com.gehu.safety.entity.PostCategory;

public interface CommunityPostService {

    PostDetailResponse createPost(Long authorUserId, CreatePostRequest request);

    PageResponse<PostSummaryResponse> getFeed(Long callerUserId, PostCategory category,
                                               String keyword, String sort, int page, int size);

    PostDetailResponse getPostDetail(Long callerUserId, Long postId);
}
