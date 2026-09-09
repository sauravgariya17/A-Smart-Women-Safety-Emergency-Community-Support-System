import React, { useState } from "react";
import { Link } from "react-router-dom";
import { MessageCircle, Flag, MapPin } from "lucide-react";
import CategoryBadge from "./CategoryBadge";
import VoteButtons from "./VoteButtons";
import ReportModal from "./ReportModal";
import { CommunityApi } from "../../../api/communityApi";

export default function PostCard({ post, onVoteChange }) {
  const [showReport, setShowReport] = useState(false);
  const [voteState, setVoteState] = useState({
    upvoteCount: post.upvoteCount,
    downvoteCount: post.downvoteCount,
    myVote: post.myVote,
  });

  const handleVote = async (value) => {
    const result = await CommunityApi.votePost(post.id, value);
    // Optimistic-ish refresh: re-derive counts locally from the toggle result.
    setVoteState((prev) => {
      const wasVote = prev.myVote;
      let up = prev.upvoteCount;
      let down = prev.downvoteCount;
      if (wasVote === 1) up -= 1;
      if (wasVote === -1) down -= 1;
      if (result === 1) up += 1;
      if (result === -1) down += 1;
      return { upvoteCount: up, downvoteCount: down, myVote: result };
    });
    onVoteChange?.();
  };

  return (
    <div className="flex gap-3 bg-white border border-gray-200 rounded-lg p-4 hover:shadow-sm transition-shadow">
      <VoteButtons
        upvoteCount={voteState.upvoteCount}
        downvoteCount={voteState.downvoteCount}
        myVote={voteState.myVote}
        onVote={handleVote}
      />

      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2 text-xs text-gray-500 mb-1">
          {/* Only the anonymous handle is ever shown - never a real name */}
          <span className="font-medium text-gray-700">u/{post.anonymousHandle}</span>
          <span>&middot;</span>
          <span>{new Date(post.createdAt).toLocaleDateString()}</span>
          {post.areaLabel && (
            <>
              <span>&middot;</span>
              <span className="flex items-center gap-1">
                <MapPin size={12} /> {post.areaLabel}
              </span>
            </>
          )}
        </div>

        <Link to={`/community/posts/${post.id}`} className="block">
          <h3 className="text-base font-semibold text-gray-900 hover:text-purple-700">{post.title}</h3>
          <p className="text-sm text-gray-600 mt-1 line-clamp-2">{post.excerpt}</p>
        </Link>

        <div className="flex items-center gap-4 mt-2">
          <CategoryBadge category={post.category} />
          <Link
            to={`/community/posts/${post.id}`}
            className="flex items-center gap-1 text-xs text-gray-500 hover:text-purple-700"
          >
            <MessageCircle size={14} /> {post.commentCount} comments
          </Link>
          <button
            onClick={() => setShowReport(true)}
            className="flex items-center gap-1 text-xs text-gray-400 hover:text-red-600"
          >
            <Flag size={14} /> Report
          </button>
        </div>
      </div>

      {showReport && (
        <ReportModal
          targetType="POST"
          targetId={post.id}
          onClose={() => setShowReport(false)}
        />
      )}
    </div>
  );
}
