import React, { useEffect, useState } from "react";
import { AlertTriangle, ShieldCheck, X } from "lucide-react";
import { SosApi } from "../../api/sosApi";
import CategoryModal from "./components/CategoryModal";
import ConfirmationCountdown from "./components/ConfirmationCountdown";

const STATE = {
  IDLE: "IDLE",
  PICKING_CATEGORY: "PICKING_CATEGORY",
  GETTING_LOCATION: "GETTING_LOCATION",
  PENDING_CONFIRMATION: "PENDING_CONFIRMATION",
  ACTIVE: "ACTIVE",
};

export default function SosButton() {
  const [state, setState] = useState(STATE.IDLE);
  const [sos, setSos] = useState(null);
  const [error, setError] = useState("");

  // On mount, resume an already-active SOS (e.g. user refreshed the page).
  useEffect(() => {
    SosApi.getActive().then((active) => {
      if (active) {
        setSos(active);
        setState(active.status === "PENDING_CONFIRMATION" ? STATE.PENDING_CONFIRMATION : STATE.ACTIVE);
      }
    });
  }, []);

  // Poll for status changes (e.g. security staff acknowledging) while non-terminal.
  useEffect(() => {
    if (state !== STATE.ACTIVE) return;
    const interval = setInterval(() => {
      SosApi.getActive().then((active) => {
        if (!active) {
          setState(STATE.IDLE);
          setSos(null);
        } else {
          setSos(active);
        }
      });
    }, 5000);
    return () => clearInterval(interval);
  }, [state]);

  const handleCategorySelected = (category) => {
    setError("");
    setState(STATE.GETTING_LOCATION);

    if (!("geolocation" in navigator)) {
      setError("Location services are not available on this device. SOS requires a location to notify contacts accurately.");
      setState(STATE.IDLE);
      return;
    }

    navigator.geolocation.getCurrentPosition(
      async (pos) => {
        try {
          const created = await SosApi.trigger({
            category,
            lat: pos.coords.latitude,
            lng: pos.coords.longitude,
          });
          setSos(created);
          setState(STATE.PENDING_CONFIRMATION);
        } catch (err) {
          setError(err?.response?.data?.message || "Could not start SOS. Please try again.");
          setState(STATE.IDLE);
        }
      },
      () => {
        setError("Location permission denied. SOS needs your location to notify contacts accurately - please allow it and try again.");
        setState(STATE.IDLE);
      },
      { enableHighAccuracy: true, timeout: 10000 }
    );
  };

  const handleConfirm = async () => {
    try {
      const confirmed = await SosApi.confirm(sos.id);
      setSos(confirmed);
      setState(STATE.ACTIVE);
    } catch (err) {
      setError(err?.response?.data?.message || "Could not confirm SOS.");
      setState(STATE.IDLE);
    }
  };

  const handleCancelPending = async () => {
    try {
      await SosApi.cancel(sos.id);
    } finally {
      setState(STATE.IDLE);
      setSos(null);
    }
  };

  const handleExpired = () => {
    setState(STATE.IDLE);
    setSos(null);
    setError("Confirmation window expired. No one was notified.");
  };

  const handleImSafeNow = async () => {
    await SosApi.cancel(sos.id);
    setState(STATE.IDLE);
    setSos(null);
  };

  return (
    <>
      {/* Floating one-tap button */}
      {state === STATE.IDLE && (
        <button
          onClick={() => setState(STATE.PICKING_CATEGORY)}
          className="fixed bottom-6 right-6 z-40 w-16 h-16 rounded-full bg-red-600 text-white font-bold shadow-lg hover:bg-red-700 flex items-center justify-center"
          aria-label="Trigger SOS"
        >
          SOS
        </button>
      )}

      {state === STATE.GETTING_LOCATION && (
        <div className="fixed bottom-6 right-6 z-40 bg-white shadow-lg rounded-md px-4 py-3 text-sm text-gray-600">
          Getting your location...
        </div>
      )}

      {error && state === STATE.IDLE && (
        <div className="fixed bottom-24 right-6 z-40 max-w-xs bg-yellow-50 border border-yellow-200 text-yellow-800 text-xs rounded-md p-3 flex gap-2">
          <AlertTriangle size={14} className="shrink-0 mt-0.5" />
          <span>{error}</span>
        </div>
      )}

      {state === STATE.PICKING_CATEGORY && (
        <CategoryModal
          onSelect={handleCategorySelected}
          onClose={() => setState(STATE.IDLE)}
        />
      )}

      {state === STATE.PENDING_CONFIRMATION && sos && (
        <ConfirmationCountdown
          expiresAt={sos.confirmationExpiresAt}
          onConfirm={handleConfirm}
          onCancel={handleCancelPending}
          onExpired={handleExpired}
        />
      )}

      {state === STATE.ACTIVE && sos && (
        <div className="fixed inset-x-0 bottom-0 z-40 bg-red-600 text-white p-4 flex items-center justify-between shadow-lg">
          <div className="flex items-center gap-2">
            <span className="w-2.5 h-2.5 rounded-full bg-white animate-pulse" />
            <div>
              <p className="font-semibold text-sm">
                {sos.status === "ACKNOWLEDGED" ? "Security has acknowledged your alert" : "Emergency alert active"}
              </p>
              <p className="text-xs opacity-90">Trusted contacts have been notified.</p>
            </div>
          </div>
          <button
            onClick={handleImSafeNow}
            className="flex items-center gap-1 bg-white text-red-700 text-sm font-medium px-3 py-1.5 rounded-md hover:bg-gray-100"
          >
            <ShieldCheck size={16} /> I'm safe now
          </button>
        </div>
      )}
    </>
  );
}
