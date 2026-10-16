import React, { useEffect, useState } from "react";
import { ClipboardList } from "lucide-react";
import { IncidentApi } from "../../api/incidentApi";

const STATUS_STYLES = {
  SUBMITTED: "bg-gray-100 text-gray-600",
  UNDER_REVIEW: "bg-yellow-100 text-yellow-700",
  ACTION_TAKEN: "bg-blue-100 text-blue-700",
  CLOSED: "bg-green-100 text-green-700",
};

export default function MyIncidentsPage() {
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    IncidentApi.getMine().then((data) => {
      setIncidents(data);
      setLoading(false);
    });
  }, []);

  return (
    <div className="max-w-2xl mx-auto py-6 px-4">
      <div className="flex items-center gap-2 mb-4">
        <ClipboardList className="text-purple-600" size={22} />
        <h1 className="text-lg font-bold text-gray-900">My Incident Reports</h1>
      </div>

      {loading ? (
        <p className="text-sm text-gray-400">Loading...</p>
      ) : incidents.length === 0 ? (
        <p className="text-sm text-gray-400">You haven't submitted any incident reports.</p>
      ) : (
        <div className="space-y-2">
          {incidents.map((i) => (
            <div key={i.id} className="bg-white border border-gray-200 rounded-md p-3">
              <div className="flex items-center justify-between mb-1">
                <span className="text-sm font-medium text-gray-800">{i.category.replace("_", " ")}</span>
                <span className={`text-xs px-2 py-0.5 rounded-full ${STATUS_STYLES[i.status] || "bg-gray-100"}`}>
                  {i.status.replace("_", " ")}
                </span>
              </div>
              <p className="text-sm text-gray-600 line-clamp-2">{i.description}</p>
              <div className="flex items-center gap-3 mt-1 text-xs text-gray-400">
                <span>Priority: {i.priority}</span>
                {i.aiSuggestedCategory && i.aiSuggestedCategory !== i.category && (
                  <span>AI suggested: {i.aiSuggestedCategory.replace("_", " ")}</span>
                )}
                <span>{new Date(i.createdAt).toLocaleDateString()}</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
