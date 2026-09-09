import React, { useState } from "react";
import { CommunityApi } from "../../../api/communityApi";

export default function ReportModal({ targetType, targetId, onClose, onReported }) {
  const [reason, setReason] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async () => {
    setSubmitting(true);
    setError("");
    try {
      await CommunityApi.reportContent({ targetType, targetId, reason });
      onReported?.();
      onClose();
    } catch (err) {
      setError(err?.response?.data?.message || "Could not submit report. Please try again.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-lg w-full max-w-md p-5">
        <h3 className="text-lg font-semibold text-gray-800 mb-2">Report this {targetType.toLowerCase()}</h3>
        <p className="text-sm text-gray-500 mb-3">
          Your report is confidential. The person who posted this will not be told who reported it.
        </p>
        <textarea
          className="w-full border border-gray-300 rounded-md p-2 text-sm mb-3 focus:outline-none focus:ring-2 focus:ring-purple-400"
          rows={3}
          placeholder="Why are you reporting this? (optional)"
          value={reason}
          onChange={(e) => setReason(e.target.value)}
        />
        {error && <p className="text-sm text-red-600 mb-2">{error}</p>}
        <div className="flex justify-end gap-2">
          <button
            className="px-3 py-1.5 text-sm rounded-md border border-gray-300 text-gray-600 hover:bg-gray-50"
            onClick={onClose}
          >
            Cancel
          </button>
          <button
            className="px-3 py-1.5 text-sm rounded-md bg-red-600 text-white hover:bg-red-700 disabled:opacity-50"
            disabled={submitting}
            onClick={handleSubmit}
          >
            {submitting ? "Submitting..." : "Submit report"}
          </button>
        </div>
      </div>
    </div>
  );
}
