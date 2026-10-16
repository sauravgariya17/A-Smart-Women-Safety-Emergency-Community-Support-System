import React, { useEffect, useState } from "react";
import { LayoutDashboard, AlertTriangle, ShieldAlert, MessageSquare, Flag, AlertCircle } from "lucide-react";
import { DashboardApi } from "../../api/adminApi";

function StatCard({ label, value, tone = "default" }) {
  const toneClass = {
    default: "text-gray-900",
    danger: "text-red-600",
    warning: "text-orange-600",
    success: "text-green-600",
  }[tone];

  return (
    <div className="bg-white border border-gray-200 rounded-lg p-4">
      <p className="text-xs text-gray-500">{label}</p>
      <p className={`text-2xl font-bold ${toneClass}`}>{value}</p>
    </div>
  );
}

export default function AdminDashboardHome() {
  const [data, setData] = useState(null);
  const [windowDays, setWindowDays] = useState(30);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    DashboardApi.getSummary(windowDays).then(setData).finally(() => setLoading(false));
  }, [windowDays]);

  if (loading || !data) {
    return <p className="text-center text-sm text-gray-400 py-10">Loading dashboard...</p>;
  }

  const maxCategoryCount = Math.max(1, ...Object.values(data.incidentsByCategory));
  const maxTrend = Math.max(1, ...data.incidentTrend.map((t) => t.count));

  return (
    <div className="max-w-4xl mx-auto py-6 px-4">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <LayoutDashboard className="text-purple-600" size={24} />
          <h1 className="text-xl font-bold text-gray-900">Admin Dashboard</h1>
        </div>
        <select
          className="border border-gray-300 rounded-md px-2 py-1.5 text-sm"
          value={windowDays}
          onChange={(e) => setWindowDays(Number(e.target.value))}
        >
          <option value={7}>Last 7 days</option>
          <option value={30}>Last 30 days</option>
          <option value={90}>Last 90 days</option>
        </select>
      </div>

      <div className="flex items-start gap-2 bg-yellow-50 border border-yellow-200 text-yellow-800 text-xs rounded-md p-3 mb-5">
        <AlertCircle size={14} className="mt-0.5 shrink-0" />
        <span>{data.disclaimer}</span>
      </div>

      {/* Summary cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-6">
        <StatCard label="Total Incidents" value={data.totalIncidents} />
        <StatCard label="Open Incidents" value={data.openIncidents} tone="warning" />
        <StatCard label="Resolved Incidents" value={data.resolvedIncidents} tone="success" />
        <StatCard label="Active Emergencies" value={data.activeEmergencyEvents} tone="danger" />
        <StatCard label="Total Emergencies" value={data.totalEmergencyEvents} />
        <StatCard label="Community Posts" value={data.totalCommunityPosts} />
        <StatCard label="Pending Post Review" value={data.pendingModerationPosts} tone="warning" />
        <StatCard label="Moderation Pending" value={data.moderationPending} tone="warning" />
      </div>

      <div className="grid md:grid-cols-2 gap-6">
        <div>
          <h2 className="text-sm font-semibold text-gray-700 mb-2 flex items-center gap-1">
            <AlertTriangle size={14} /> Incidents by Category
          </h2>
          <div className="space-y-2">
            {Object.entries(data.incidentsByCategory).map(([cat, count]) => (
              <div key={cat}>
                <div className="flex justify-between text-xs text-gray-500 mb-0.5">
                  <span>{cat.replace("_", " ")}</span>
                  <span>{count}</span>
                </div>
                <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                  <div className="h-full bg-purple-500 rounded-full" style={{ width: `${(count / maxCategoryCount) * 100}%` }} />
                </div>
              </div>
            ))}
            {Object.keys(data.incidentsByCategory).length === 0 && (
              <p className="text-xs text-gray-400">No incidents in this window.</p>
            )}
          </div>
        </div>

        <div>
          <h2 className="text-sm font-semibold text-gray-700 mb-2 flex items-center gap-1">
            <Flag size={14} /> Moderation Breakdown
          </h2>
          <ul className="text-sm text-gray-700 space-y-1">
            <li className="flex justify-between"><span>Pending</span><span className="font-medium">{data.moderationPending}</span></li>
            <li className="flex justify-between"><span>Approved</span><span className="font-medium">{data.moderationApproved}</span></li>
            <li className="flex justify-between"><span>Removed</span><span className="font-medium">{data.moderationRemoved}</span></li>
            <li className="flex justify-between text-gray-500"><span>Flagged by AI</span><span>{data.moderationAiFlagged}</span></li>
            <li className="flex justify-between text-gray-500"><span>Flagged by users</span><span>{data.moderationUserFlagged}</span></li>
          </ul>

          <h2 className="text-sm font-semibold text-gray-700 mt-4 mb-2 flex items-center gap-1">
            <MessageSquare size={14} /> Frequently Reported Areas
          </h2>
          <ul className="text-sm text-gray-700 space-y-1">
            {Object.entries(data.incidentsByArea).map(([area, count]) => (
              <li key={area} className="flex justify-between"><span>{area}</span><span className="font-medium">{count}</span></li>
            ))}
            {Object.keys(data.incidentsByArea).length === 0 && (
              <li className="text-xs text-gray-400">No areas meet the minimum-report threshold yet.</li>
            )}
          </ul>
        </div>
      </div>

      <div className="mt-6">
        <h2 className="text-sm font-semibold text-gray-700 mb-2 flex items-center gap-1">
          <ShieldAlert size={14} /> Incident Trend
        </h2>
        <div className="flex items-end gap-1 h-20">
          {data.incidentTrend.map((d) => (
            <div key={d.date} className="flex-1 flex flex-col items-center justify-end" title={`${d.date}: ${d.count}`}>
              <div
                className="w-full bg-purple-400 rounded-t"
                style={{ height: `${(d.count / maxTrend) * 100}%`, minHeight: d.count > 0 ? "4px" : "0" }}
              />
            </div>
          ))}
          {data.incidentTrend.length === 0 && <p className="text-xs text-gray-400">No data yet.</p>}
        </div>
      </div>
    </div>
  );
}
