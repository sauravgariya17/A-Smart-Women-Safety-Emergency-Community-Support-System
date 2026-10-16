import React, { useEffect, useState } from "react";
import { FileClock } from "lucide-react";
import { AuditLogApi } from "../../api/adminApi";

export default function AuditLogPage() {
  const [logs, setLogs] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [actionType, setActionType] = useState("");
  const [loading, setLoading] = useState(true);

  const load = (targetPage = 0) => {
    setLoading(true);
    AuditLogApi.search({ actionType: actionType || undefined, page: targetPage, size: 25 })
      .then((data) => {
        setLogs(data.content);
        setPage(data.page);
        setTotalPages(data.totalPages);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [actionType]);

  return (
    <div className="max-w-4xl mx-auto py-6 px-4">
      <div className="flex items-center gap-2 mb-1">
        <FileClock className="text-purple-600" size={22} />
        <h1 className="text-lg font-bold text-gray-900">Audit Log</h1>
      </div>
      <p className="text-xs text-gray-400 mb-4">
        Immutable record of sensitive actions across the platform (role changes, moderation decisions, SOS handling, incident overrides).
      </p>

      <input
        className="border border-gray-300 rounded-md px-2 py-1.5 text-sm mb-4 w-64"
        placeholder="Filter by action type (e.g. USER_ROLE_CHANGED)"
        value={actionType}
        onChange={(e) => setActionType(e.target.value)}
      />

      {loading ? (
        <p className="text-sm text-gray-400">Loading...</p>
      ) : (
        <div className="space-y-1.5">
          {logs.map((log) => (
            <div key={log.id} className="bg-white border border-gray-200 rounded-md p-2.5 text-sm">
              <div className="flex justify-between">
                <span className="font-medium text-gray-800">{log.actionType}</span>
                <span className="text-xs text-gray-400">{new Date(log.timestamp).toLocaleString()}</span>
              </div>
              <p className="text-xs text-gray-500 mt-0.5">
                By {log.actorName}
                {log.targetType && ` · ${log.targetType}${log.targetId ? ` #${log.targetId}` : ""}`}
              </p>
              {log.reason && <p className="text-xs text-gray-600 mt-1">{log.reason}</p>}
            </div>
          ))}
          {logs.length === 0 && <p className="text-sm text-gray-400">No audit entries match this filter.</p>}
        </div>
      )}

      {totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-4">
          <button disabled={page === 0} onClick={() => load(page - 1)} className="px-3 py-1 text-sm rounded-md border border-gray-300 disabled:opacity-40">
            Previous
          </button>
          <span className="text-sm text-gray-500 py-1">Page {page + 1} of {totalPages}</span>
          <button disabled={page + 1 >= totalPages} onClick={() => load(page + 1)} className="px-3 py-1 text-sm rounded-md border border-gray-300 disabled:opacity-40">
            Next
          </button>
        </div>
      )}
    </div>
  );
}
