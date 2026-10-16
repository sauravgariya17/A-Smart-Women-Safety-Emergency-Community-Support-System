import React, { useState } from "react";
import { AlertTriangle, Sparkles, CheckCircle2 } from "lucide-react";
import { IncidentApi, INCIDENT_CATEGORIES, VISIBILITY_OPTIONS } from "../../api/incidentApi";

const STAGE = { WRITING: "WRITING", REVIEWING: "REVIEWING", SUBMITTED: "SUBMITTED" };

/**
 * Two-step flow: (1) describe -> AI analyzes and SUGGESTS category/priority/summary
 * as a preview only (nothing saved yet); (2) reporter reviews, can change the
 * category, picks visibility, and submits for real. This is what makes the
 * AI a "recommendation, not a decision" in the actual UX, not just in prose.
 */
export default function ReportIncidentPage() {
  const [stage, setStage] = useState(STAGE.WRITING);
  const [description, setDescription] = useState("");
  const [analysis, setAnalysis] = useState(null);
  const [category, setCategory] = useState("");
  const [visibilityType, setVisibilityType] = useState("ANON_PUBLIC");
  const [areaLabel, setAreaLabel] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleAnalyze = async (e) => {
    e.preventDefault();
    setError("");
    if (description.trim().length < 10) {
      setError("Please share a bit more detail so we can help route this correctly.");
      return;
    }
    setLoading(true);
    try {
      const result = await IncidentApi.analyze(description);
      setAnalysis(result);
      setCategory(result.suggestedCategory);
      setStage(STAGE.REVIEWING);
    } catch (err) {
      setError(err?.response?.data?.message || "Could not analyze your report. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async () => {
    setLoading(true);
    setError("");
    try {
      await IncidentApi.create({ description, category, visibilityType, areaLabel });
      setStage(STAGE.SUBMITTED);
    } catch (err) {
      setError(err?.response?.data?.message || "Could not submit your report. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  if (stage === STAGE.SUBMITTED) {
    return (
      <div className="max-w-xl mx-auto py-16 px-4 text-center">
        <CheckCircle2 className="mx-auto text-green-600 mb-3" size={40} />
        <h1 className="text-lg font-bold text-gray-900 mb-1">Report submitted</h1>
        <p className="text-sm text-gray-500">
          Thank you for reporting this. You can track its status under "My Reports".
        </p>
      </div>
    );
  }

  return (
    <div className="max-w-xl mx-auto py-6 px-4">
      <div className="flex items-center gap-2 mb-1">
        <AlertTriangle className="text-purple-600" size={22} />
        <h1 className="text-lg font-bold text-gray-900">Report an Incident</h1>
      </div>
      <p className="text-sm text-gray-500 mb-4">
        This is a formal, trackable report reviewed by campus safety staff - different from a community post.
      </p>

      {stage === STAGE.WRITING && (
        <form onSubmit={handleAnalyze} className="space-y-3">
          <textarea
            className="w-full border border-gray-300 rounded-md p-3 text-sm"
            rows={6}
            placeholder="Describe what happened, when, and where, in as much detail as you're comfortable sharing..."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
          {error && <p className="text-sm text-red-600">{error}</p>}
          <button
            type="submit"
            disabled={loading}
            className="w-full py-2.5 rounded-md bg-purple-600 text-white font-medium hover:bg-purple-700 disabled:opacity-50 flex items-center justify-center gap-2"
          >
            <Sparkles size={16} /> {loading ? "Analyzing..." : "Continue"}
          </button>
        </form>
      )}

      {stage === STAGE.REVIEWING && analysis && (
        <div className="space-y-4">
          <div className="bg-purple-50 border border-purple-200 rounded-md p-3">
            <p className="text-xs font-medium text-purple-700 flex items-center gap-1 mb-1">
              <Sparkles size={12} /> AI suggestion (you can change this)
            </p>
            <p className="text-sm text-gray-800">
              Category: <strong>{analysis.suggestedCategory.replace("_", " ")}</strong>{" "}
              <span className="text-gray-400">({Math.round(analysis.confidence * 100)}% confidence)</span>
            </p>
            <p className="text-xs text-gray-500 mt-1">{analysis.categoryReasoning}</p>
            <p className="text-sm text-gray-800 mt-2">
              Suggested priority: <strong>{analysis.suggestedPriority}</strong>
            </p>
            <p className="text-xs text-gray-500 mt-1">{analysis.priorityReasoning}</p>
            {analysis.summary && (
              <p className="text-xs text-gray-500 mt-2 italic">Summary for reviewers: {analysis.summary}</p>
            )}
            <p className="text-xs text-purple-600 mt-2">
              This is an AI-assisted suggestion, not a final decision. Campus staff will review this report.
            </p>
          </div>

          <div>
            <label className="text-sm font-medium text-gray-700">Category (your choice)</label>
            <select
              className="mt-1 w-full border border-gray-300 rounded-md p-2 text-sm"
              value={category}
              onChange={(e) => setCategory(e.target.value)}
            >
              {INCIDENT_CATEGORIES.map((c) => (
                <option key={c.value} value={c.value}>{c.label}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="text-sm font-medium text-gray-700">Who can see your identity?</label>
            <select
              className="mt-1 w-full border border-gray-300 rounded-md p-2 text-sm"
              value={visibilityType}
              onChange={(e) => setVisibilityType(e.target.value)}
            >
              {VISIBILITY_OPTIONS.map((v) => (
                <option key={v.value} value={v.value}>{v.label}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="text-sm font-medium text-gray-700">General area (optional)</label>
            <input
              className="mt-1 w-full border border-gray-300 rounded-md p-2 text-sm"
              placeholder="e.g. Near Main Gate - never exact GPS"
              value={areaLabel}
              onChange={(e) => setAreaLabel(e.target.value)}
            />
          </div>

          {error && <p className="text-sm text-red-600">{error}</p>}

          <div className="flex gap-2">
            <button
              onClick={() => setStage(STAGE.WRITING)}
              className="flex-1 py-2.5 rounded-md border border-gray-300 text-gray-600 hover:bg-gray-50"
            >
              Back
            </button>
            <button
              onClick={handleSubmit}
              disabled={loading}
              className="flex-1 py-2.5 rounded-md bg-purple-600 text-white font-medium hover:bg-purple-700 disabled:opacity-50"
            >
              {loading ? "Submitting..." : "Submit Report"}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
