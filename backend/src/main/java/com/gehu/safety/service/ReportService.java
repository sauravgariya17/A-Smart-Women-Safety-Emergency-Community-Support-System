package com.gehu.safety.service;

import com.gehu.safety.dto.request.ReportContentRequest;

public interface ReportService {
    void reportContent(Long reporterUserId, ReportContentRequest request);
}
