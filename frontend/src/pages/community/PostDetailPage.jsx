import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { ArrowLeft, MapPin, Flag } from "lucide-react";
import { CommunityApi } from "../../api/communityApi";
import CategoryBadge from "./components/CategoryBadge";
import VoteButtons from "./components/VoteButtons";
import CommentList from "./components/CommentList";
import ReportModal from "./components/ReportModal";

export default function PostDetailPage() {
  const { postId } = useParams();
  const [post, setPost] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showReport, setShowReport] = useState(false);

  const loadPost = async () => {
    setLoading(true);
    const data = await CommunityApi.getPostDetail(postId);
    setPost(data);
    setLoading(false);
  };

  useEffect(() => {
    loadPost();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [postId]);

  const handleVote = async (value) => {
    const result = await CommunityApi.votePost(postId, value);
    setPost((prev) => {
      let up = prev.upvoteCount;
      let down = prev.downvoteCount;
      if (prev.myVote === 1) up -= 1;
      if (prev.myVote === -1) down -= 1;
      if (result === 1) up += 1;
      if (result === -1) down += 1;
      return { ...prev, upvoteCount: up, downvoteCount: down, myVote: result };
    });
  };

  if (loading) return <p className="text-center text-sm text-gray-400 py-10">Loading...</p>;
  if (!post) return <p className="text-center text-sm text-gray-400 py-10">Post not found.</p>;

  return (
    <div className="max-w-2xl mx-auto py-6 px-4">
      <Link to="/community" className="flex items-center gap-1 text-sm text-purple-600 mb-4 hover:underline">
        <ArrowLeft size={16} /> Back to community
      </Link>

      <div className="bg-white border border-gray-200 rounded-lg p-5 flex gap-4">
        <VoteButtons
          upvoteCount={post.upvoteCount}
          downvoteCount={post.downvoteCount}
          myVote={post.myVote}
          onVote={handleVote}
        />
        <div className="flex-1">
          <div className="flex items-center gap-2 text-xs text-gray-500 mb-1">
            <span className="font-medium text-gray-700">u/{post.anonymousHandle}</span>
            <span>&middot;</span>
            <span>{new Date(post.createdAt).toLocaleString()}</span>
            {post.areaLabel && (
              <>
                <span>&middot;</span>
                <span className="flex items-center gap-1"><MapPin size={12} /> {post.areaLabel}</span>
              </>
            )}
            <button
              onClick={() => setShowReport(true)}
              className="ml-auto flex items-center gap-1 text-gray-400 hover:text-red-600"
            >
              <Flag size={12} /> Report
            </button>
          </div>

          <h1 className="text-lg font-bold text-gray-900 mb-2">{post.title}</h1>
          <CategoryBadge category={post.category} />
          <p className="text-sm text-gray-800 mt-3 whitespace-pre-wrap">{post.body}</p>
        </div>
      </div>

      <CommentList postId={postId} />

      {showReport && (
        <ReportModal targetType="POST" targetId={post.id} onClose={() => setShowReport(false)} />
      )}
    </div>
  );
}
