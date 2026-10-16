import React from "react";
import { SOS_CATEGORIES } from "../../../api/sosApi";

export default function CategoryModal({ onSelect, onClose }) {
  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-lg w-full max-w-sm p-5">
        <h3 className="text-lg font-semibold text-gray-800 mb-1">What's happening?</h3>
        <p className="text-sm text-gray-500 mb-4">This helps campus security respond appropriately.</p>

        <div className="grid grid-cols-2 gap-2">
          {SOS_CATEGORIES.map((c) => (
            <button
              key={c.value}
              onClick={() => onSelect(c.value)}
              className="p-3 text-sm rounded-md border border-gray-300 hover:border-red-500 hover:bg-red-50 text-gray-700 font-medium"
            >
              {c.label}
            </button>
          ))}
        </div>

        <button
          onClick={onClose}
          className="w-full mt-4 py-2 text-sm text-gray-500 hover:text-gray-700"
        >
          Cancel
        </button>
      </div>
    </div>
  );
}
