package com.gehu.safety.service;

import com.gehu.safety.dto.request.TrustedContactRequest;
import com.gehu.safety.dto.response.TrustedContactResponse;

import java.util.List;

public interface TrustedContactService {
    TrustedContactResponse addContact(Long userId, TrustedContactRequest request);
    List<TrustedContactResponse> getContacts(Long userId);
    TrustedContactResponse updateContact(Long userId, Long contactId, TrustedContactRequest request);
    void deleteContact(Long userId, Long contactId);
}
