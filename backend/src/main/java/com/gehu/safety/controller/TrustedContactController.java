package com.gehu.safety.controller;

import com.gehu.safety.dto.request.TrustedContactRequest;
import com.gehu.safety.dto.response.ApiResponse;
import com.gehu.safety.dto.response.TrustedContactResponse;
import com.gehu.safety.security.UserPrincipal;
import com.gehu.safety.service.TrustedContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * All endpoints are implicitly scoped to the authenticated user's own contacts.
 * There is no "list all contacts" admin endpoint here by design - trusted
 * contacts are private data, only ever readable by their owner.
 */
@RestController
@RequestMapping("/api/contacts")
public class TrustedContactController {

    private final TrustedContactService trustedContactService;


    public TrustedContactController(TrustedContactService trustedContactService) {
        this.trustedContactService = trustedContactService;
    }
    @PostMapping
    public ResponseEntity<ApiResponse<TrustedContactResponse>> addContact(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TrustedContactRequest request) {

        TrustedContactResponse response = trustedContactService.addContact(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Trusted contact added", response));
    }

    @GetMapping
    public ApiResponse<List<TrustedContactResponse>> getContacts(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(trustedContactService.getContacts(principal.getId()));
    }

    @PutMapping("/{contactId}")
    public ApiResponse<TrustedContactResponse> updateContact(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long contactId,
            @Valid @RequestBody TrustedContactRequest request) {

        return ApiResponse.success("Trusted contact updated",
                trustedContactService.updateContact(principal.getId(), contactId, request));
    }

    @DeleteMapping("/{contactId}")
    public ApiResponse<Void> deleteContact(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long contactId) {

        trustedContactService.deleteContact(principal.getId(), contactId);
        return ApiResponse.success("Trusted contact removed", null);
    }
}
