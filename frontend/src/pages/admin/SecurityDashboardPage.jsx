import React, { useEffect, useState } from "react";
import { ShieldAlert, Phone, MapPin, CheckCircle } from "lucide-react";
import { SosManagementApi } from "../../api/sosApi";

const REFRESH_INTERVAL_MS = 8000;

export default function SecurityDashboardPage() {
  const [queue, setQueue] = useState([]);
  const [loading, setLoading] = useState(true);
  const [resolvingId, setResolvingId] = useState(null);
  const [notes, setNotes] = useState("");

  const load = () => {
    SosManagementApi.getActiveQueue()
      .then(setQueue)
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
    const interval = setInterval(load, REFRESH_INTERVAL_MS);
    return () => clearInterval(interval);
  }, []);

  const handleAcknowledge = async (id) => {
    await SosManagementApi.acknowledge(id);
    load();
  };

  const handleResolve = async (id, outcome) => {
    await SosManagementApi.resolve(id, outcome, notes);
    setResolvingId(null);
    setNotes("");
    load();
  };

  return (
    <div className="max-w-3xl mx-auto py-6 px-4">
      <div className="flex items-center gap-2 mb-1">
        <ShieldAlert className="text-red-600" size={24} />
        <h1 className="text-xl font-bold text-gray-900">Campus Security Dashboard</h1>
      </div>
      <p className="text-xs text-gray-400 mb-5">
        This is a GEHU campus security console, not a connection to police or external emergency services.
      </p>

      {loading ? (
        <p className="text-sm text-gray-400">Loading active alerts...</p>
      ) : queue.length === 0 ? (
        <p className="text-sm text-gray-400">No active emergencies right now.</p>
      ) : (
        <div className="space-y-3">
          {queue.map((s) => (
            <div key={s.id} className="bg-white border border-red-200 rounded-lg p-4">
              <div className="flex items-center justify-between mb-2">
                <span className="font-semibold text-red-700">{s.category.replace("_", " ")}</span>
                <span className="text-xs px-2 py-0.5 rounded-full bg-red-100 text-red-700">{s.status}</span>
              </div>

              <p className="text-sm text-gray-800 font-medium">{s.requesterName}</p>
              <p className="text-xs text-gray-500 flex items-center gap-1 mt-0.5">
                <Phone size={12} /> {s.requesterPhone || "No phone on file"}
              </p>
              <p className="text-xs text-gray-500 flex items-center gap-1 mt-0.5">
                <MapPin size={12} /> {s.lat.toFixed(6)}, {s.lng.toFixed(6)}
              </p>
              <p className="text-xs text-gray-400 mt-1">
                Triggered: {new Date(s.triggeredAt).toLocaleString()}
              </p>

              <div className="flex gap-2 mt-3">
                {s.status === "TRIGGERED" && (
                  <button
                    onClick={() => handleAcknowledge(s.id)}
                    className="flex items-center gap-1 text-sm px-3 py-1.5 rounded-md bg-orange-600 text-white hover:bg-orange-700"
                  >
                    <CheckCircle size={14} /> Acknowledge
                  </button>
                )}
                {s.status === "ACKNOWLEDGED" && resolvingId !== s.id && (
                  <button
                    onClick={() => setResolvingId(s.id)}
                    className="text-sm px-3 py-1.5 rounded-md bg-green-600 text-white hover:bg-green-700"
                  >
                    Close this alert
                  </button>
                )}
              </div>

              {resolvingId === s.id && (
                <div className="mt-3 border-t border-gray-100 pt-3">
                  <textarea
                    className="w-full border border-gray-300 rounded-md p-2 text-sm mb-2"
                    rows={2}
                    placeholder="Resolution notes (what happened, action taken)..."
                    value={notes}
                    onChange={(e) => setNotes(e.target.value)}
                  />
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleResolve(s.id, "RESOLVED")}
                      className="text-sm px-3 py-1.5 rounded-md bg-green-600 text-white hover:bg-green-700"
                    >
                      Mark Resolved
                    </button>
                    <button
                      onClick={() => handleResolve(s.id, "FALSE_ALARM")}
                      className="text-sm px-3 py-1.5 rounded-md bg-gray-200 text-gray-700 hover:bg-gray-300"
                    >
                      Mark False Alarm
                    </button>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
