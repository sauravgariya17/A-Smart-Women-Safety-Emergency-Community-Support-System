import React, { useEffect, useRef, useState } from "react";
import { CheckCircle, XCircle, Link as LinkIcon, AlertTriangle } from "lucide-react";
import CampusMap from "../map/CampusMap";
import { useGeolocation } from "../map/useGeolocation";
import { JourneyApi } from "../../api/journeyApi";

const PING_INTERVAL_MS = 30_000; // periodic, not continuous surveillance - tied to an explicit active journey only

export default function ActiveJourneyView({ journey, onEnded }) {
  const { position, status: geoStatus, error: geoError } = useGeolocation({ watch: true });
  const [current, setCurrent] = useState(journey);
  const [busy, setBusy] = useState(false);
  const [pingError, setPingError] = useState("");
  const [copied, setCopied] = useState(false);
  const lastPingRef = useRef(0);

  // Send a location ping at most once per PING_INTERVAL_MS while the
  // journey is active AND we have a real position. This stops the moment
  // the journey ends (component unmounts / status changes) - no background
  // tracking beyond this explicit, user-started journey.
  useEffect(() => {
    if (!position || current.status !== "ACTIVE") return;

    const now = Date.now();
    if (now - lastPingRef.current < PING_INTERVAL_MS) return;
    lastPingRef.current = now;

    JourneyApi.ping(current.id, position.lat, position.lng)
      .then((updated) => {
        setCurrent(updated);
        setPingError("");
      })
      .catch(() => {
        // Graceful network/API failure: keep the last known state, warn softly,
        // and let the next interval tick retry rather than crashing the view.
        setPingError("Could not update your live location just now. Retrying automatically...");
      });
  }, [position, current.id, current.status]);

  const handleCheckIn = async () => {
    setBusy(true);
    try {
      await JourneyApi.checkIn(current.id);
      onEnded();
    } finally {
      setBusy(false);
    }
  };

  const handleCancel = async () => {
    setBusy(true);
    try {
      await JourneyApi.cancel(current.id);
      onEnded();
    } finally {
      setBusy(false);
    }
  };

  const shareLink = `${window.location.origin}${current.shareUrl}`;
  const copyLink = () => {
    navigator.clipboard.writeText(shareLink);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="max-w-2xl mx-auto py-6 px-4">
      <div className="flex items-center gap-2 mb-1">
        <span className="w-2.5 h-2.5 rounded-full bg-green-500 animate-pulse" />
        <h1 className="text-xl font-bold text-gray-900">Journey in progress</h1>
      </div>
      <p className="text-sm text-gray-500 mb-4">
        Expected arrival: {new Date(current.expectedArrival).toLocaleTimeString()}
      </p>

      {geoStatus === "denied" && (
        <div className="flex items-start gap-2 bg-yellow-50 border border-yellow-200 text-yellow-800 text-sm rounded-md p-3 mb-4">
          <AlertTriangle size={16} className="mt-0.5 shrink-0" />
          <span>{geoError} Your last known position on the map may be out of date.</span>
        </div>
      )}
      {pingError && (
        <div className="text-xs text-yellow-700 bg-yellow-50 border border-yellow-200 rounded-md px-3 py-1.5 mb-3">
          {pingError}
        </div>
      )}

      <CampusMap
        currentPosition={position}
        selectedDestination={{ lat: current.destLat, lng: current.destLng }}
        liveMarker={{ lat: current.lastKnownLat, lng: current.lastKnownLng }}
      />

      <div className="mt-4 bg-gray-50 border border-gray-200 rounded-md p-3">
        <p className="text-xs text-gray-500 mb-1 flex items-center gap-1">
          <LinkIcon size={12} /> Share this link with trusted contacts to let them follow along:
        </p>
        <div className="flex gap-2">
          <input readOnly value={shareLink} className="flex-1 text-xs border border-gray-300 rounded p-1.5 bg-white" />
          <button onClick={copyLink} className="text-xs px-2 py-1 rounded bg-gray-200 hover:bg-gray-300">
            {copied ? "Copied!" : "Copy"}
          </button>
        </div>
        {current.notifiedContactNames?.length > 0 && (
          <p className="text-xs text-gray-400 mt-1">
            Already sharing with: {current.notifiedContactNames.join(", ")}
          </p>
        )}
      </div>

      <div className="flex gap-2 mt-4">
        <button
          onClick={handleCheckIn}
          disabled={busy}
          className="flex-1 flex items-center justify-center gap-1 py-2.5 rounded-md bg-green-600 text-white font-medium hover:bg-green-700 disabled:opacity-50"
        >
          <CheckCircle size={16} /> I've reached safely
        </button>
        <button
          onClick={handleCancel}
          disabled={busy}
          className="flex-1 flex items-center justify-center gap-1 py-2.5 rounded-md bg-gray-200 text-gray-700 font-medium hover:bg-gray-300 disabled:opacity-50"
        >
          <XCircle size={16} /> Cancel journey
        </button>
      </div>
    </div>
  );
}
