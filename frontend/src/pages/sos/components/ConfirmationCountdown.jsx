import React, { useEffect, useState } from "react";

/**
 * Mirrors the backend's confirmation_expires_at exactly - the countdown
 * shown here is cosmetic, the real enforcement is server-side (SosServiceImpl
 * rejects a confirm() call after expiry regardless of what the UI shows).
 */
export default function ConfirmationCountdown({ expiresAt, onConfirm, onCancel, onExpired }) {
  const [secondsLeft, setSecondsLeft] = useState(
    Math.max(0, Math.round((new Date(expiresAt) - new Date()) / 1000))
  );

  useEffect(() => {
    if (secondsLeft <= 0) {
      onExpired?.();
      return;
    }
    const timer = setTimeout(() => setSecondsLeft((s) => s - 1), 1000);
    return () => clearTimeout(timer);
  }, [secondsLeft, onExpired]);

  return (
    <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-lg w-full max-w-sm p-6 text-center">
        <p className="text-sm text-gray-500 mb-2">Confirm within</p>
        <div className="text-5xl font-bold text-red-600 mb-4">{secondsLeft}s</div>
        <p className="text-sm text-gray-600 mb-5">
          Tap Confirm if this is a real emergency. Your trusted contacts will be notified immediately.
        </p>
        <button
          onClick={onConfirm}
          className="w-full py-3 rounded-md bg-red-600 text-white font-bold text-lg hover:bg-red-700 mb-2"
        >
          CONFIRM EMERGENCY
        </button>
        <button
          onClick={onCancel}
          className="w-full py-2 text-sm text-gray-500 hover:text-gray-700"
        >
          It was a mistake, cancel
        </button>
      </div>
    </div>
  );
}
