package com.gehu.safety.service;

import com.gehu.safety.entity.TargetType;

public interface VoteService {

    /**
     * Casts, changes, or removes a vote (toggle semantics: sending the same
     * value again removes it). Recomputes and persists the denormalized
     * upvote/downvote counters on the target post/comment.
     *
     * @return the caller's resulting vote value: -1, 0, or 1
     */
    int castVote(Long userId, TargetType targetType, Long targetId, int voteValue);

    /** Used by response-mapping code to show "my vote" on each item. */
    int getMyVote(Long userId, TargetType targetType, Long targetId);
}
