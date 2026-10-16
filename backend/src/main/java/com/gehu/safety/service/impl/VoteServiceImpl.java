package com.gehu.safety.service.impl;

import com.gehu.safety.entity.*;
import com.gehu.safety.exception.ResourceNotFoundException;
import com.gehu.safety.repository.CommentRepository;
import com.gehu.safety.repository.CommunityPostRepository;
import com.gehu.safety.repository.VoteRepository;
import com.gehu.safety.service.VoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final CommunityPostRepository postRepository;
    private final CommentRepository commentRepository;


    public VoteServiceImpl(VoteRepository voteRepository,
            CommunityPostRepository postRepository,
            CommentRepository commentRepository) {
        this.voteRepository = voteRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }
    @Override
    @Transactional
    public int castVote(Long userId, TargetType targetType, Long targetId, int voteValue) {
        ensureTargetExists(targetType, targetId);

        var existing = voteRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);

        int resultingVote;
        if (existing.isPresent()) {
            Vote vote = existing.get();
            if (voteValue == 0 || vote.getVoteValue() == voteValue) {
                // toggle off
                voteRepository.delete(vote);
                resultingVote = 0;
            } else {
                vote.setVoteValue(voteValue);
                voteRepository.save(vote);
                resultingVote = voteValue;
            }
        } else if (voteValue != 0) {
            Vote vote = Vote.builder()
                    .user(userRef(userId))
                    .targetType(targetType)
                    .targetId(targetId)
                    .voteValue(voteValue)
                    .build();
            voteRepository.save(vote);
            resultingVote = voteValue;
        } else {
            resultingVote = 0;
        }

        recomputeCounters(targetType, targetId);
        return resultingVote;
    }

    @Override
    public int getMyVote(Long userId, TargetType targetType, Long targetId) {
        if (userId == null) return 0;
        return voteRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)
                .map(Vote::getVoteValue)
                .orElse(0);
    }

    private void recomputeCounters(TargetType targetType, Long targetId) {
        long upvotes = voteRepository.countByTargetTypeAndTargetIdAndVoteValue(targetType, targetId, 1);
        long downvotes = voteRepository.countByTargetTypeAndTargetIdAndVoteValue(targetType, targetId, -1);

        if (targetType == TargetType.POST) {
            CommunityPost post = postRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
            post.setUpvoteCount((int) upvotes);
            post.setDownvoteCount((int) downvotes);
            postRepository.save(post);
        } else {
            Comment comment = commentRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
            comment.setUpvoteCount((int) upvotes);
            comment.setDownvoteCount((int) downvotes);
            commentRepository.save(comment);
        }
    }

    private void ensureTargetExists(TargetType targetType, Long targetId) {
        boolean exists = (targetType == TargetType.POST)
                ? postRepository.existsById(targetId)
                : commentRepository.existsById(targetId);
        if (!exists) {
            throw new ResourceNotFoundException(targetType + " not found");
        }
    }

    /** Lightweight reference to avoid an extra full User fetch just to set the FK. */
    private User userRef(Long userId) {
        User u = new User();
        u.setId(userId);
        return u;
    }
}
