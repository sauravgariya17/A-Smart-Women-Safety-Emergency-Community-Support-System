import React, { useEffect, useState } from "react";
import { Navigation, AlertTriangle, Users } from "lucide-react";
import CampusMap from "../map/CampusMap";
import { useGeolocation } from "../map/useGeolocation";
import { JourneyApi } from "../../api/journeyApi";
import api from "../../api/axiosInstance";
import ActiveJourneyView from "./ActiveJourneyView";

export default function JourneyModePage() {
  const { position, status, error } = useGeolocation();
  const [manualStart, setManualStart] = useState(null);
  const [destination, setDestination] = useState(null);
  const [duration, setDuration] = useState(15);
  const [contacts, setContacts] = useState([]);
  const [selectedContactIds, setSelectedContactIds] = useState([]);
  const [activeJourney, setActiveJourney] = useState(undefined); // undefined = loading, null = none
  const [starting, setStarting] = useState(false);
  const [formError, setFormError] = useState("");

  const effectiveStart = manualStart || position;

  useEffect(() => {
    JourneyApi.getActive().then(setActiveJourney);
    api.get("/contacts").then((res) => setContacts(res.data.data)).catch(() => setContacts([]));
  }, []);

  const toggleContact = (id) => {
    setSelectedContactIds((prev) =>
      prev.includes(id) ? prev.filter((c) => c !== id) : [...prev, id]
    );
  };

  const handleStart = async () => {
    setFormError("");
    if (!effectiveStart) {
      setFormError("We need a start point. Please allow location access or tap the map to set one manually.");
      return;
    }
    if (!destination) {
      setFormError("Please select a destination on the map.");
      return;
    }

    setStarting(true);
    try {
      const journey = await JourneyApi.start({
        startLat: effectiveStart.lat,
        startLng: effectiveStart.lng,
        destLat: destination.lat,
        destLng: destination.lng,
        expectedDurationMinutes: duration,
        trustedContactIds: selectedContactIds,
      });
      setActiveJourney(journey);
    } catch (err) {
      setFormError(err?.response?.data?.message || "Could not start journey. Please try again.");
    } finally {
      setStarting(false);
    }
  };

  // If there's already an active journey (e.g. page refresh mid-journey), show that instead.
  if (activeJourney === undefined) {
    return <p className="text-center text-sm text-gray-400 py-10">Loading...</p>;
  }
  if (activeJourney) {
    return <ActiveJourneyView journey={activeJourney} onEnded={() => setActiveJourney(null)} />;
  }

  return (
    <div className="max-w-2xl mx-auto py-6 px-4">
      <div className="flex items-center gap-2 mb-1">
        <Navigation className="text-purple-600" size={24} />
        <h1 className="text-xl font-bold text-gray-900">Journey Mode</h1>
      </div>
      <p className="text-sm text-gray-500 mb-4">
        Share a live check-in with trusted contacts while you walk across or near campus.
      </p>

      {status === "denied" && (
        <div className="flex items-start gap-2 bg-yellow-50 border border-yellow-200 text-yellow-800 text-sm rounded-md p-3 mb-4">
          <AlertTriangle size={16} className="mt-0.5 shrink-0" />
          <span>{error} Tap the map below to set your starting point manually.</span>
        </div>
      )}
      {status === "unavailable" && (
        <div className="flex items-start gap-2 bg-yellow-50 border border-yellow-200 text-yellow-800 text-sm rounded-md p-3 mb-4">
          <AlertTriangle size={16} className="mt-0.5 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      <CampusMap
        currentPosition={effectiveStart}
        selectedDestination={destination}
        selectable={true}
        onSelectDestination={(lat, lng) => {
          // First tap sets start (only needed if geolocation failed), second tap sets destination.
          if (!effectiveStart) {
            setManualStart({ lat, lng });
          } else {
            setDestination({ lat, lng });
          }
        }}
      />

      <div className="mt-4 space-y-3">
        <div>
          <label className="text-sm font-medium text-gray-700">Expected duration (minutes)</label>
          <input
            type="number"
            min={1}
            max={180}
            value={duration}
            onChange={(e) => setDuration(Number(e.target.value))}
            className="mt-1 w-32 border border-gray-300 rounded-md p-2 text-sm block"
          />
        </div>

        <div>
          <label className="text-sm font-medium text-gray-700 flex items-center gap-1 mb-1">
            <Users size={14} /> Notify these trusted contacts
          </label>
          {contacts.length === 0 ? (
            <p className="text-xs text-gray-400">
              You haven't added any trusted contacts yet. You can still start a journey, but no one will be notified.
            </p>
          ) : (
            <div className="flex flex-wrap gap-2">
              {contacts.map((c) => (
                <button
                  key={c.id}
                  type="button"
                  onClick={() => toggleContact(c.id)}
                  className={`text-xs px-3 py-1.5 rounded-full border ${
                    selectedContactIds.includes(c.id)
                      ? "bg-purple-600 text-white border-purple-600"
                      : "bg-white text-gray-600 border-gray-300"
                  }`}
                >
                  {c.name}
                </button>
              ))}
            </div>
          )}
        </div>

        {formError && <p className="text-sm text-red-600">{formError}</p>}

        <button
          onClick={handleStart}
          disabled={starting}
          className="w-full py-2.5 rounded-md bg-purple-600 text-white font-medium hover:bg-purple-700 disabled:opacity-50"
        >
          {starting ? "Starting..." : "Start Journey"}
        </button>
      </div>
    </div>
  );
}
