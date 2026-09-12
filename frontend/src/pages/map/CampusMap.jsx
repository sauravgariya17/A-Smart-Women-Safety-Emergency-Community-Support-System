import React, { useEffect, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup, Circle, useMapEvents } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { MapApi } from "../../api/mapApi";

// Default Leaflet marker icons don't load correctly with bundlers unless
// re-pointed like this - a well-known Leaflet+Vite/Webpack gotcha.
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});

const safeZoneIcon = new L.Icon({
  iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
  iconSize: [20, 33],
  className: "safe-zone-marker", // style green via CSS if desired
});

/** Registers click handling for destination selection without prop-drilling into MapContainer. */
function ClickHandler({ onMapClick }) {
  useMapEvents({
    click(e) {
      onMapClick?.(e.latlng.lat, e.latlng.lng);
    },
  });
  return null;
}

/**
 * @param currentPosition {lat, lng} | null - the user's live GPS position
 * @param selectedDestination {lat, lng} | null - controlled from parent
 * @param onSelectDestination (lat, lng) => void - fired on map click, only if selectable=true
 * @param selectable boolean - whether clicking the map picks a destination
 * @param liveMarker {lat, lng} | null - an optional extra marker, e.g. journey's last known position
 */
export default function CampusMap({
  currentPosition = null,
  selectedDestination = null,
  onSelectDestination,
  selectable = false,
  liveMarker = null,
}) {
  const [mapConfig, setMapConfig] = useState(null);
  const [safeZones, setSafeZones] = useState([]);
  const [loadError, setLoadError] = useState(null);

  useEffect(() => {
    Promise.all([MapApi.getMapConfig(), MapApi.getSafeZones()])
      .then(([config, zones]) => {
        setMapConfig(config);
        setSafeZones(zones);
      })
      .catch(() => {
        // Graceful network/API failure handling: fall back to a hardcoded
        // GEHU center so the map still renders, just without live safe-zone data.
        setMapConfig({ centerLat: 30.2675, centerLng: 77.9959, boundsRadiusKm: 2.0 });
        setLoadError("Could not load live safe-zone data. Showing the base campus map only.");
      });
  }, []);

  if (!mapConfig) {
    return <div className="h-96 flex items-center justify-center text-sm text-gray-400">Loading map...</div>;
  }

  const center = [mapConfig.centerLat, mapConfig.centerLng];
  // Rough km-to-degree conversion for a bounding box around the campus center.
  const deg = mapConfig.boundsRadiusKm / 111;
  const bounds = [
    [center[0] - deg, center[1] - deg],
    [center[0] + deg, center[1] + deg],
  ];

  return (
    <div>
      {loadError && (
        <div className="text-xs text-yellow-700 bg-yellow-50 border border-yellow-200 rounded-md px-3 py-1.5 mb-2">
          {loadError}
        </div>
      )}
      <MapContainer
        center={center}
        zoom={16}
        minZoom={14}
        maxBounds={bounds}
        maxBoundsViscosity={1.0}
        style={{ height: "420px", width: "100%", borderRadius: "0.5rem" }}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />

        {/* Visualizes the enforced service-area boundary, not just decoration */}
        <Circle
          center={center}
          radius={mapConfig.boundsRadiusKm * 1000}
          pathOptions={{ color: "#9333ea", fillOpacity: 0.03, weight: 1, dashArray: "4" }}
        />

        {safeZones.map((zone) => (
          <Marker key={zone.id} position={[zone.lat, zone.lng]} icon={safeZoneIcon}>
            <Popup>
              <strong>{zone.name}</strong>
              <br />
              {zone.type.replace("_", " ")}
              {zone.description && <p className="text-xs mt-1">{zone.description}</p>}
            </Popup>
          </Marker>
        ))}

        {currentPosition && (
          <Marker position={[currentPosition.lat, currentPosition.lng]}>
            <Popup>Your current location</Popup>
          </Marker>
        )}

        {selectedDestination && (
          <Marker position={[selectedDestination.lat, selectedDestination.lng]}>
            <Popup>Destination</Popup>
          </Marker>
        )}

        {liveMarker && (
          <Marker position={[liveMarker.lat, liveMarker.lng]}>
            <Popup>Last known position</Popup>
          </Marker>
        )}

        {selectable && <ClickHandler onMapClick={(lat, lng) => onSelectDestination?.(lat, lng)} />}
      </MapContainer>
      {selectable && (
        <p className="text-xs text-gray-400 mt-1">Tap anywhere on the map to set your destination.</p>
      )}
    </div>
  );
}
