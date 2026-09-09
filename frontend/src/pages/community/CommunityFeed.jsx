import React, { useEffect, useState } from "react";
import { Search, Plus, ShieldAlert } from "lucide-react";
import { CommunityApi, POST_CATEGORIES } from "../../api/communityApi";
import PostCard from "./components/PostCard";
import CreatePostForm from "./components/CreatePostForm";

export default function CommunityFeed() {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [category, setCategory] = useState("");
  const [keyword, setKeyword] = useState("");
  const [sort, setSort] = useState("new");
  const [loading, setLoading] = useState(true);
  const [showCreateForm, setShowCreateForm] = useState(false);

  const loadFeed = async (targetPage = 0) => {
    setLoading(true);
    const data = await CommunityApi.getFeed({
      category: category || undefined,
      keyword: keyword || undefined,
      sort,
      page: targetPage,
      size: 10,
    });
    setPosts(data.content);
    setPage(data.page);
    setTotalPages(data.totalPages);
    setLoading(false);
  };

  useEffect(() => {
    loadFeed(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [category, sort]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    loadFeed(0);
  };

  return (
    <div className="max-w-2xl mx-auto py-6 px-4">
      <div className="flex items-center gap-2 mb-4">
        <ShieldAlert className="text-purple-600" size={24} />
        <h1 className="text-xl font-bold text-gray-900">GEHU Safety Community</h1>
      </div>
      <p className="text-sm text-gray-500 mb-5">
        Share safety incidents and support each other, anonymously. Your real name is never shown here.
      </p>

      {/* Search + filters */}
      <form onSubmit={handleSearchSubmit} className="flex flex-wrap gap-2 mb-4">
        <div className="flex-1 min-w-[180px] relative">
          <Search size={16} className="absolute left-2 top-2.5 text-gray-400" />
          <input
            className="w-full border border-gray-300 rounded-md pl-8 pr-2 py-2 text-sm"
            placeholder="Search posts..."
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
        </div>
        <select
          className="border border-gray-300 rounded-md px-2 py-2 text-sm"
          value={category}
          onChange={(e) => setCategory(e.target.value)}
        >
          <option value="">All categories</option>
          {POST_CATEGORIES.map((c) => (
            <option key={c.value} value={c.value}>{c.label}</option>
          ))}
        </select>
        <select
          className="border border-gray-300 rounded-md px-2 py-2 text-sm"
          value={sort}
          onChange={(e) => setSort(e.target.value)}
        >
          <option value="new">Newest</option>
          <option value="top">Top voted</option>
        </select>
        <button
          type="button"
          onClick={() => setShowCreateForm((s) => !s)}
          className="flex items-center gap-1 px-3 py-2 text-sm rounded-md bg-purple-600 text-white hover:bg-purple-700"
        >
          <Plus size={16} /> New post
        </button>
      </form>

      {showCreateForm && (
        <div className="mb-5">
          <CreatePostForm
            onCancel={() => setShowCreateForm(false)}
            onCreated={() => {
              setShowCreateForm(false);
              loadFeed(0);
            }}
          />
        </div>
      )}

      {/* Feed */}
      {loading ? (
        <p className="text-sm text-gray-400">Loading posts...</p>
      ) : (
        <div className="space-y-3">
          {posts.map((post) => (
            <PostCard key={post.id} post={post} onVoteChange={() => {}} />
          ))}
          {posts.length === 0 && (
            <p className="text-sm text-gray-400 text-center py-10">
              No posts yet. Be the first to share something with the community.
            </p>
          )}
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-6">
          <button
            disabled={page === 0}
            onClick={() => loadFeed(page - 1)}
            className="px-3 py-1 text-sm rounded-md border border-gray-300 disabled:opacity-40"
          >
            Previous
          </button>
          <span className="text-sm text-gray-500 py-1">
            Page {page + 1} of {totalPages}
          </span>
          <button
            disabled={page + 1 >= totalPages}
            onClick={() => loadFeed(page + 1)}
            className="px-3 py-1 text-sm rounded-md border border-gray-300 disabled:opacity-40"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}
