import React, { useEffect, useState } from "react";
import { Flag } from "lucide-react";
import { CommunityApi } from "../../../api/communityApi";
import VoteButtons from "./VoteButtons";
import ReportModal from "./ReportModal";

export default function CommentList({ postId, refreshKey }) {
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [newComment, setNewComment] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [reportTarget, setReportTarget] = useState(null);

  const loadComments = async () => {
    setLoading(true);
    const data = await CommunityApi.getComments(postId);
    setComments(data.content);
    setLoading(false);
  };

  useEffect(() => {
    loadComments();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [postId, refreshKey]);

  const handleAddComment = async (e) => {
    e.preventDefault();
    if (newComment.trim().length === 0) return;
    setSubmitting(true);
    try {
      await CommunityApi.addComment(postId, newComment.trim());
      setNewComment("");
      await loadComments();
    } finally {
      setSubmitting(false);
    }
  };

  const handleVote = async (commentId, value, comment) => {
    const result = await CommunityApi.voteComment(commentId, value);
    setComments((prev) =>
      prev.map((c) => {
        if (c.id !== commentId) return c;
        let up = c.upvoteCount;
        let down = c.downvoteCount;
        if (c.myVote === 1) up -= 1;
        if (c.myVote === -1) down -= 1;
        if (result === 1) up += 1;
        if (result === -1) down += 1;
        return { ...c, upvoteCount: up, downvoteCount: down, myVote: result };
      })
    );
  };

  return (
    <div className="mt-6">
      <h4 className="text-sm font-semibold text-gray-700 mb-3">
        {comments.length} {comments.length === 1 ? "Comment" : "Comments"}
      </h4>

      <form onSubmit={handleAddComment} className="flex gap-2 mb-4">
        <input
          className="flex-1 border border-gray-300 rounded-md p-2 text-sm"
          placeholder="Add a supportive comment..."
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          maxLength={2000}
        />
        <button
          type="submit"
          disabled={submitting}
          className="px-3 py-1.5 text-sm rounded-md bg-purple-600 text-white hover:bg-purple-700 disabled:opacity-50"
        >
          Reply
        </button>
      </form>

      {loading ? (
        <p className="text-sm text-gray-400">Loading comments...</p>
      ) : (
        <div className="space-y-3">
          {comments.map((c) => (
            <div key={c.id} className="flex gap-3 border-t border-gray-100 pt-3">
              <VoteButtons
                orientation="horizontal"
                upvoteCount={c.upvoteCount}
                downvoteCount={c.downvoteCount}
                myVote={c.myVote}
                onVote={(v) => handleVote(c.id, v, c)}
              />
              <div className="flex-1">
                <div className="flex items-center gap-2 text-xs text-gray-500">
                  <span className="font-medium text-gray-700">u/{c.anonymousHandle}</span>
                  <span>&middot;</span>
                  <span>{new Date(c.createdAt).toLocaleDateString()}</span>
                  <button
                    onClick={() => setReportTarget(c.id)}
                    className="ml-auto flex items-center gap-1 text-gray-400 hover:text-red-600"
                  >
                    <Flag size={12} /> Report
                  </button>
                </div>
                <p className="text-sm text-gray-800 mt-1">{c.body}</p>
              </div>
            </div>
          ))}
          {comments.length === 0 && (
            <p className="text-sm text-gray-400">No comments yet. Be the first to respond.</p>
          )}
        </div>
      )}

      {reportTarget && (
        <ReportModal
          targetType="COMMENT"
          targetId={reportTarget}
          onClose={() => setReportTarget(null)}
        />
      )}
    </div>
  );
}
