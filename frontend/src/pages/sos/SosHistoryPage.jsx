import React, { useEffect, useState } from "react";
import { ShieldAlert } from "lucide-react";
import { SosApi } from "../../api/sosApi";

const STATUS_STYLES = {
  PENDING_CONFIRMATION: "bg-gray-100 text-gray-600",
  TRIGGERED: "bg-red-100 text-red-700",
  ACKNOWLEDGED: "bg-orange-100 text-orange-700",
  RESOLVED: "bg-green-100 text-green-700",
  FALSE_ALARM: "bg-blue-100 text-blue-700",
  CANCELLED: "bg-gray-100 text-gray-500",
};

export default function SosHistoryPage() {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    SosApi.getHistory().then((data) => {
      setHistory(data);
      setLoading(false);
    });
  }, []);

  return (
    <div className="max-w-2xl mx-auto py-6 px-4">
      <div className="flex items-center gap-2 mb-4">
        <ShieldAlert className="text-red-600" size={22} />
        <h1 className="text-lg font-bold text-gray-900">My SOS History</h1>
      </div>

      {loading ? (
        <p className="text-sm text-gray-400">Loading...</p>
      ) : history.length === 0 ? (
        <p className="text-sm text-gray-400">No SOS events yet. That's a good thing.</p>
      ) : (
        <div className="space-y-2">
          {history.map((s) => (
            <div key={s.id} className="bg-white border border-gray-200 rounded-md p-3">
              <div className="flex items-center justify-between mb-1">
                <span className="text-sm font-medium text-gray-800">{s.category.replace("_", " ")}</span>
                <span className={`text-xs px-2 py-0.5 rounded-full ${STATUS_STYLES[s.status] || "bg-gray-100"}`}>
                  {s.status.replace("_", " ")}
                </span>
              </div>
              <p className="text-xs text-gray-500">{new Date(s.createdAt).toLocaleString()}</p>
              {s.notes && <p className="text-xs text-gray-600 mt-1">{s.notes}</p>}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
