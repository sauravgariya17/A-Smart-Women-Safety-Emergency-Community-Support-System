import React, { useEffect, useState } from "react";
import { ShieldAlert, User, MapPin } from "lucide-react";
import { IncidentAdminApi, INCIDENT_CATEGORIES } from "../../api/incidentApi";

const PRIORITY_STYLES = {
  LOW: "bg-gray-100 text-gray-600",
  MEDIUM: "bg-yellow-100 text-yellow-700",
  HIGH: "bg-orange-100 text-orange-700",
  CRITICAL: "bg-red-100 text-red-700",
};

export default function IncidentAdminPage() {
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterCategory, setFilterCategory] = useState("");
  const [filterStatus, setFilterStatus] = useState("");

  const load = () => {
    setLoading(true);
    IncidentAdminApi.getAll({ category: filterCategory || undefined, status: filterStatus || undefined })
      .then((data) => setIncidents(data.content))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filterCategory, filterStatus]);

  const updateStatus = async (id, status) => {
    await IncidentAdminApi.review(id, { status });
    load();
  };

  const overridePriority = async (id, priority) => {
    await IncidentAdminApi.review(id, { priority });
    load();
  };

  return (
    <div className="max-w-3xl mx-auto py-6 px-4">
      <div className="flex items-center gap-2 mb-1">
        <ShieldAlert className="text-purple-600" size={24} />
        <h1 className="text-xl font-bold text-gray-900">Incident Review Queue</h1>
      </div>
      <p className="text-xs text-gray-400 mb-4">
        AI-suggested category/priority is shown alongside the current value - override anytime.
      </p>

      <div className="flex gap-2 mb-4">
        <select className="border border-gray-300 rounded-md px-2 py-1.5 text-sm" value={filterCategory} onChange={(e) => setFilterCategory(e.target.value)}>
          <option value="">All categories</option>
          {INCIDENT_CATEGORIES.map((c) => <option key={c.value} value={c.value}>{c.label}</option>)}
        </select>
        <select className="border border-gray-300 rounded-md px-2 py-1.5 text-sm" value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)}>
          <option value="">All statuses</option>
          <option value="SUBMITTED">Submitted</option>
          <option value="UNDER_REVIEW">Under Review</option>
          <option value="ASSIGNED">Assigned</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="RESOLVED">Resolved</option>
          <option value="CLOSED">Closed</option>
        </select>
      </div>

      {loading ? (
        <p className="text-sm text-gray-400">Loading...</p>
      ) : (
        <div className="space-y-3">
          {incidents.map((i) => (
            <div key={i.id} className="bg-white border border-gray-200 rounded-lg p-4">
              <div className="flex items-center justify-between mb-2">
                <span className="font-semibold text-gray-800">{i.category.replace("_", " ")}</span>
                <span className={`text-xs px-2 py-0.5 rounded-full ${PRIORITY_STYLES[i.priority]}`}>{i.priority}</span>
              </div>

              {i.reporterName ? (
                <p className="text-xs text-gray-500 flex items-center gap-1 mb-1">
                  <User size={12} /> {i.reporterName} (identity shared per visibility setting)
                </p>
              ) : (
                <p className="text-xs text-gray-400 mb-1">Reporter identity not shared for this report</p>
              )}
              {i.areaLabel && (
                <p className="text-xs text-gray-500 flex items-center gap-1 mb-1">
                  <MapPin size={12} /> {i.areaLabel}
                </p>
              )}

              <p className="text-sm text-gray-800 mt-2">{i.description}</p>
              {i.aiSummary && (
                <p className="text-xs text-gray-500 mt-2 italic">AI summary: {i.aiSummary}</p>
              )}

              <div className="text-xs text-gray-400 mt-2">
                AI originally suggested: {i.aiSuggestedCategory?.replace("_", " ")} / {i.aiSuggestedPriority}
                {" "}({Math.round((i.aiConfidence || 0) * 100)}% confidence)
              </div>

              <div className="flex flex-wrap gap-2 mt-3">
                {["LOW", "MEDIUM", "HIGH", "CRITICAL"].map((p) => (
                  <button
                    key={p}
                    onClick={() => overridePriority(i.id, p)}
                    className={`text-xs px-2 py-1 rounded-full border ${
                      i.priority === p ? "bg-purple-600 text-white border-purple-600" : "bg-white text-gray-600 border-gray-300"
                    }`}
                  >
                    {p}
                  </button>
                ))}
              </div>

              {i.assignedToName && (
                <p className="text-xs text-gray-500 mt-1">Assigned to: {i.assignedToName}</p>
              )}

              <div className="flex gap-2 mt-2 flex-wrap">
                {["UNDER_REVIEW", "ASSIGNED", "IN_PROGRESS", "RESOLVED", "CLOSED"].map((s) => (
                  <button
                    key={s}
                    onClick={() => updateStatus(i.id, s)}
                    className="text-xs px-2 py-1 rounded-md bg-gray-100 hover:bg-gray-200 text-gray-700"
                  >
                    Mark {s.replace("_", " ")}
                  </button>
                ))}
              </div>
            </div>
          ))}
          {incidents.length === 0 && <p className="text-sm text-gray-400">No incidents match this filter.</p>}
        </div>
      )}
    </div>
  );
}
