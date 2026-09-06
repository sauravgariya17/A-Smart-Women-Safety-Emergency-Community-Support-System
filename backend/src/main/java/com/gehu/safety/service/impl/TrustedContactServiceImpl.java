package com.gehu.safety.service.impl;

import com.gehu.safety.dto.request.TrustedContactRequest;
import com.gehu.safety.dto.response.TrustedContactResponse;
import com.gehu.safety.entity.TrustedContact;
import com.gehu.safety.entity.User;
import com.gehu.safety.exception.BadRequestException;
import com.gehu.safety.exception.ResourceNotFoundException;
import com.gehu.safety.repository.TrustedContactRepository;
import com.gehu.safety.repository.UserRepository;
import com.gehu.safety.service.TrustedContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrustedContactServiceImpl implements TrustedContactService {

    private static final int MAX_CONTACTS = 5;

    private final TrustedContactRepository trustedContactRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TrustedContactResponse addContact(Long userId, TrustedContactRequest request) {
        if (trustedContactRepository.countByUserId(userId) >= MAX_CONTACTS) {
            throw new BadRequestException("You can add up to " + MAX_CONTACTS + " trusted contacts only");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TrustedContact contact = TrustedContact.builder()
                .user(user)
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .relation(request.getRelation())
                .priority(request.getPriority() != null ? request.getPriority() : 1)
                .verified(false) // TODO Stage 2: send verification link/OTP to the contact
                .build();

        trustedContactRepository.save(contact);
        return toResponse(contact);
    }

    @Override
    public List<TrustedContactResponse> getContacts(Long userId) {
        return trustedContactRepository.findByUserIdOrderByPriorityAsc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TrustedContactResponse updateContact(Long userId, Long contactId, TrustedContactRequest request) {
        TrustedContact contact = trustedContactRepository.findByIdAndUserId(contactId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trusted contact not found"));

        contact.setName(request.getName());
        contact.setPhone(request.getPhone());
        contact.setEmail(request.getEmail());
        contact.setRelation(request.getRelation());
        if (request.getPriority() != null) {
            contact.setPriority(request.getPriority());
        }

        trustedContactRepository.save(contact);
        return toResponse(contact);
    }

    @Override
    @Transactional
    public void deleteContact(Long userId, Long contactId) {
        TrustedContact contact = trustedContactRepository.findByIdAndUserId(contactId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trusted contact not found"));
        trustedContactRepository.delete(contact);
    }

    private TrustedContactResponse toResponse(TrustedContact contact) {
        return TrustedContactResponse.builder()
                .id(contact.getId())
                .name(contact.getName())
                .phone(contact.getPhone())
                .email(contact.getEmail())
                .relation(contact.getRelation())
                .priority(contact.getPriority())
                .verified(contact.isVerified())
                .build();
    }
}
