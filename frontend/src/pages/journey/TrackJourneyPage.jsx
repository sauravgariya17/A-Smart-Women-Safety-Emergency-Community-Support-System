import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { ShieldAlert, MapPin } from "lucide-react";
import CampusMap from "../map/CampusMap";
import { JourneyApi } from "../../api/journeyApi";

const REFRESH_INTERVAL_MS = 15_000;

/**
 * This page requires NO login - the token in the URL is the only credential.
 * It shows exactly what JourneyShareResponse contains: destination, last
 * known position, expected arrival, status. No name, no account details.
 */
export default function TrackJourneyPage() {
  const { token } = useParams();
  const [journey, setJourney] = useState(undefined); // undefined = loading, null = expired/invalid
  const [error, setError] = useState("");

  const load = () => {
    JourneyApi.track(token)
      .then(setJourney)
      .catch(() => {
        setJourney(null);
        setError("This tracking link is invalid or the journey has already ended.");
      });
  };

  useEffect(() => {
    load();
    const interval = setInterval(load, REFRESH_INTERVAL_MS);
    return () => clearInterval(interval);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  if (journey === undefined) {
    return <p className="text-center text-sm text-gray-400 py-10">Loading...</p>;
  }

  if (journey === null) {
    return (
      <div className="max-w-md mx-auto py-16 px-4 text-center">
        <ShieldAlert className="mx-auto text-gray-300 mb-3" size={40} />
        <p className="text-sm text-gray-500">{error}</p>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto py-6 px-4">
      <div className="flex items-center gap-2 mb-1">
        <ShieldAlert className="text-purple-600" size={22} />
        <h1 className="text-lg font-bold text-gray-900">Live Journey Tracking</h1>
      </div>
      <p className="text-sm text-gray-500 mb-4">
        You're viewing this because someone shared their journey with you. This page updates automatically.
      </p>

      <div className="bg-white border border-gray-200 rounded-md p-3 mb-3 flex items-center gap-2 text-sm">
        <MapPin size={16} className="text-purple-600" />
        <span className="font-medium">Status:</span> {journey.status}
        <span className="text-gray-400">&middot;</span>
        <span>Expected by {new Date(journey.expectedArrival).toLocaleTimeString()}</span>
      </div>

      <CampusMap
        selectedDestination={{ lat: journey.destLat, lng: journey.destLng }}
        liveMarker={
          journey.lastKnownLat != null
            ? { lat: journey.lastKnownLat, lng: journey.lastKnownLng }
            : null
        }
      />

      {journey.lastPingAt && (
        <p className="text-xs text-gray-400 mt-2">
          Last updated: {new Date(journey.lastPingAt).toLocaleTimeString()}
        </p>
      )}
    </div>
  );
}
