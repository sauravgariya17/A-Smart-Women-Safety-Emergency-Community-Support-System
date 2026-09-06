package com.gehu.safety.repository;

import com.gehu.safety.entity.TrustedContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrustedContactRepository extends JpaRepository<TrustedContact, Long> {
    List<TrustedContact> findByUserIdOrderByPriorityAsc(Long userId);
    Optional<TrustedContact> findByIdAndUserId(Long id, Long userId);
    long countByUserId(Long userId);
}
