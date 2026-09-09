package com.gehu.safety.service;

import com.gehu.safety.dto.request.ModerationReviewRequest;
import com.gehu.safety.dto.response.ModerationQueueItemResponse;
import com.gehu.safety.dto.response.PageResponse;

public interface ModerationService {
    PageResponse<ModerationQueueItemResponse> getPendingQueue(int page, int size);
    void reviewItem(Long moderatorUserId, Long queueId, ModerationReviewRequest request);
}
