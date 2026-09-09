package com.gehu.safety.repository;

import com.gehu.safety.entity.TargetType;
import com.gehu.safety.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);
    long countByTargetTypeAndTargetIdAndVoteValue(TargetType targetType, Long targetId, Integer voteValue);
    void deleteByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);
}
