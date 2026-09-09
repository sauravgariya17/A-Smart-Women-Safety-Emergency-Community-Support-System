import React, { useState } from "react";
import { CommunityApi, POST_CATEGORIES } from "../../../api/communityApi";

export default function CreatePostForm({ onCreated, onCancel }) {
  const [title, setTitle] = useState("");
  const [body, setBody] = useState("");
  const [category, setCategory] = useState(POST_CATEGORIES[0].value);
  const [areaLabel, setAreaLabel] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (title.trim().length < 5 || body.trim().length < 10) {
      setError("Please write a bit more detail before posting.");
      return;
    }

    setSubmitting(true);
    try {
      const post = await CommunityApi.createPost({ title, body, category, areaLabel });
      onCreated?.(post);
    } catch (err) {
      setError(err?.response?.data?.message || "Could not create post. Please try again.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white border border-gray-200 rounded-lg p-4 space-y-3">
      <div className="flex items-center justify-between">
        <h3 className="text-base font-semibold text-gray-800">Share anonymously</h3>
        <p className="text-xs text-gray-400">Your name is never shown - only your handle.</p>
      </div>

      <select
        className="w-full border border-gray-300 rounded-md p-2 text-sm"
        value={category}
        onChange={(e) => setCategory(e.target.value)}
      >
        {POST_CATEGORIES.map((c) => (
          <option key={c.value} value={c.value}>{c.label}</option>
        ))}
      </select>

      <input
        className="w-full border border-gray-300 rounded-md p-2 text-sm"
        placeholder="Title (what happened, briefly)"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        maxLength={200}
      />

      <textarea
        className="w-full border border-gray-300 rounded-md p-2 text-sm"
        rows={5}
        placeholder="Share the details that could help other students stay safe..."
        value={body}
        onChange={(e) => setBody(e.target.value)}
        maxLength={5000}
      />

      <input
        className="w-full border border-gray-300 rounded-md p-2 text-sm"
        placeholder="General area (optional, e.g. 'Near Main Gate') - never exact GPS"
        value={areaLabel}
        onChange={(e) => setAreaLabel(e.target.value)}
        maxLength={120}
      />

      {error && <p className="text-sm text-red-600">{error}</p>}

      <div className="flex justify-end gap-2">
        <button
          type="button"
          className="px-3 py-1.5 text-sm rounded-md border border-gray-300 text-gray-600 hover:bg-gray-50"
          onClick={onCancel}
        >
          Cancel
        </button>
        <button
          type="submit"
          disabled={submitting}
          className="px-4 py-1.5 text-sm rounded-md bg-purple-600 text-white hover:bg-purple-700 disabled:opacity-50"
        >
          {submitting ? "Posting..." : "Post anonymously"}
        </button>
      </div>
    </form>
  );
}
