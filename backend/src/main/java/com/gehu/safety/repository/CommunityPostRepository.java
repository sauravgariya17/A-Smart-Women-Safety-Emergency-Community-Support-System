package com.gehu.safety.repository;

import com.gehu.safety.entity.CommunityPost;
import com.gehu.safety.entity.ModerationStatus;
import com.gehu.safety.entity.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    /**
     * Feed query: only ever returns PUBLISHED posts to regular users.
     * category/keyword are optional filters - passing null skips that filter.
     */
    @Query("SELECT p FROM CommunityPost p WHERE p.moderationStatus = :status " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(p.body) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<CommunityPost> search(@Param("status") ModerationStatus status,
                                @Param("category") PostCategory category,
                                @Param("keyword") String keyword,
                                Pageable pageable);

    // Note: findAll(Pageable) is already inherited from JpaRepository and is
    // used by the moderation service to fetch posts regardless of status.
}
