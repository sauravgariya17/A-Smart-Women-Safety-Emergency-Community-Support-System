import React, { useEffect, useState } from "react";
import { BarChart3, AlertCircle } from "lucide-react";
import { AnalyticsApi } from "../../api/incidentApi";

export default function SafetyAnalyticsPage() {
  const [data, setData] = useState(null);
  const [windowDays, setWindowDays] = useState(30);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    AnalyticsApi.getSafetyAnalytics(windowDays)
      .then(setData)
      .finally(() => setLoading(false));
  }, [windowDays]);

  const maxCategoryCount = data ? Math.max(1, ...Object.values(data.reportsByCategory)) : 1;
  const maxAreaCount = data ? Math.max(1, ...Object.values(data.reportsByArea)) : 1;

  return (
    <div className="max-w-3xl mx-auto py-6 px-4">
      <div className="flex items-center gap-2 mb-1">
        <BarChart3 className="text-purple-600" size={24} />
        <h1 className="text-xl font-bold text-gray-900">Safety Analytics</h1>
      </div>

      <select
        className="border border-gray-300 rounded-md px-2 py-1.5 text-sm mb-4"
        value={windowDays}
        onChange={(e) => setWindowDays(Number(e.target.value))}
      >
        <option value={7}>Last 7 days</option>
        <option value={30}>Last 30 days</option>
        <option value={90}>Last 90 days</option>
      </select>

      {loading || !data ? (
        <p className="text-sm text-gray-400">Loading...</p>
      ) : (
        <>
          {/* The disclaimer is never optional - always shown alongside the data, not buried in a tooltip. */}
          <div className="flex items-start gap-2 bg-yellow-50 border border-yellow-200 text-yellow-800 text-xs rounded-md p-3 mb-5">
            <AlertCircle size={14} className="mt-0.5 shrink-0" />
            <span>{data.disclaimer}</span>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            <div>
              <h2 className="text-sm font-semibold text-gray-700 mb-2">Reports by Category</h2>
              <div className="space-y-2">
                {Object.entries(data.reportsByCategory).map(([cat, count]) => (
                  <div key={cat}>
                    <div className="flex justify-between text-xs text-gray-500 mb-0.5">
                      <span>{cat.replace("_", " ")}</span>
                      <span>{count}</span>
                    </div>
                    <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                      <div
                        className="h-full bg-purple-500 rounded-full"
                        style={{ width: `${(count / maxCategoryCount) * 100}%` }}
                      />
                    </div>
                  </div>
                ))}
                {Object.keys(data.reportsByCategory).length === 0 && (
                  <p className="text-xs text-gray-400">No reports in this window.</p>
                )}
              </div>
            </div>

            <div>
              <h2 className="text-sm font-semibold text-gray-700 mb-2">Reports by Area</h2>
              <p className="text-xs text-gray-400 mb-2">
                Areas with very few reports are hidden to protect reporter privacy.
              </p>
              <div className="space-y-2">
                {Object.entries(data.reportsByArea).map(([area, count]) => (
                  <div key={area}>
                    <div className="flex justify-between text-xs text-gray-500 mb-0.5">
                      <span>{area}</span>
                      <span>{count}</span>
                    </div>
                    <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                      <div
                        className="h-full bg-orange-500 rounded-full"
                        style={{ width: `${(count / maxAreaCount) * 100}%` }}
                      />
                    </div>
                  </div>
                ))}
                {Object.keys(data.reportsByArea).length === 0 && (
                  <p className="text-xs text-gray-400">No areas meet the minimum-report threshold yet.</p>
                )}
              </div>
            </div>
          </div>

          <div className="mt-6">
            <h2 className="text-sm font-semibold text-gray-700 mb-2">Daily Trend</h2>
            <div className="flex items-end gap-1 h-24">
              {data.trend.map((d) => {
                const maxTrend = Math.max(1, ...data.trend.map((t) => t.count));
                return (
                  <div key={d.date} className="flex-1 flex flex-col items-center justify-end" title={`${d.date}: ${d.count}`}>
                    <div
                      className="w-full bg-purple-400 rounded-t"
                      style={{ height: `${(d.count / maxTrend) * 100}%`, minHeight: d.count > 0 ? "4px" : "0" }}
                    />
                  </div>
                );
              })}
              {data.trend.length === 0 && <p className="text-xs text-gray-400">No data yet.</p>}
            </div>
          </div>
        </>
      )}
    </div>
  );
}
