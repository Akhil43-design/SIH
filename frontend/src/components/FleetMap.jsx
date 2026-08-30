import React, { useEffect, useRef } from 'react';
import L from 'leaflet';
import { getVehicleColor, PRIORITY_COLORS, formatTime } from '../utils/constants';

export function FleetMap({ depots, customers, optimization, selectedVehicleId, onSelectVehicle }) {
  const mapContainerRef = useRef(null);
  const mapInstanceRef = useRef(null);
  const layersGroupRef = useRef(null);

  // Initialize Map
  useEffect(() => {
    if (!mapContainerRef.current) return;

    if (!mapInstanceRef.current) {
      const map = L.map(mapContainerRef.current, {
        center: [51.5074, -0.1278], // Default to London coordinates
        zoom: 12,
        attributionControl: false
      });

      // Dark Matter Map Tiles (CartoDB)
      L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
        maxZoom: 19,
        subdomains: 'abcd'
      }).addTo(map);

      layersGroupRef.current = L.featureGroup().addTo(map);
      mapInstanceRef.current = map;
    }

    return () => {
      // Map cleanup on unmount
      if (mapInstanceRef.current) {
        mapInstanceRef.current.remove();
        mapInstanceRef.current = null;
      }
    };
  }, []);

  // Update Markers and Routes
  useEffect(() => {
    const map = mapInstanceRef.current;
    const group = layersGroupRef.current;
    if (!map || !group) return;

    group.clearLayers();
    const bounds = L.latLngBounds([]);

    // Map depot lookup
    const depotMap = new Map();
    (depots || []).forEach(d => {
      if (d.latitude && d.longitude) {
        depotMap.set(d.id, d);
      }
    });

    // Map customer lookup
    const customerMap = new Map();
    (customers || []).forEach(c => {
      if (c.latitude && c.longitude) {
        customerMap.set(c.id, c);
      }
    });

    // 1. Draw Depots
    (depots || []).forEach(d => {
      if (!d.latitude || !d.longitude) return;
      const latLng = [d.latitude, d.longitude];
      bounds.extend(latLng);

      const icon = L.divIcon({
        className: '',
        html: `<div class="custom-depot-pin" style="width: 28px; height: 28px;">🏭</div>`,
        iconSize: [28, 28],
        iconAnchor: [14, 14]
      });

      const marker = L.marker(latLng, { icon }).addTo(group);
      marker.bindPopup(`
        <div style="font-family: var(--font-sans); font-size: 12px; line-height: 1.4;">
          <strong style="color: var(--accent-purple); font-size: 13px;">Depot: ${d.name || d.id}</strong><br/>
          <span style="color: var(--text-muted);">ID: ${d.id}</span><br/>
          <span>Location: [${d.latitude.toFixed(4)}, ${d.longitude.toFixed(4)}]</span>
        </div>
      `);
    });

    // 2. Draw Customers
    (customers || []).forEach(c => {
      if (!c.latitude || !c.longitude) return;
      const latLng = [c.latitude, c.longitude];
      bounds.extend(latLng);

      const color = PRIORITY_COLORS[c.priority] || '#3b82f6';
      const isCancelled = !!c.cancelled;

      const icon = L.divIcon({
        className: '',
        html: `
          <div class="custom-customer-pin" style="width: 22px; height: 22px; background-color: ${isCancelled ? '#64748b' : color}; ${isCancelled ? 'opacity: 0.5; text-decoration: line-through;' : ''}">
            ${c.id.replace('C', '')}
          </div>
        `,
        iconSize: [22, 22],
        iconAnchor: [11, 11]
      });

      const marker = L.marker(latLng, { icon }).addTo(group);
      marker.bindPopup(`
        <div style="font-family: var(--font-sans); font-size: 12px; line-height: 1.4;">
          <strong style="color: ${color}; font-size: 13px;">Customer: ${c.name || c.id}</strong><br/>
          <span>ID: <code>${c.id}</code> | Demand: <strong>${c.demand} kg</strong></span><br/>
          <span>Priority: <strong style="color: ${color};">${c.priority || 'MEDIUM'}</strong></span><br/>
          <span>Time Window: [${c.earliestTime || 0}m - ${c.latestTime || 1440}m]</span><br/>
          <span>Status: <strong>${isCancelled ? 'CANCELLED' : 'ACTIVE'}</strong></span>
        </div>
      `);
    });

    // 3. Draw Vehicle Routes (Polylines)
    if (optimization && optimization.vehicleRoutes) {
      optimization.vehicleRoutes.forEach((vr, idx) => {
        const vehicleColor = getVehicleColor(idx);
        const isSelected = !selectedVehicleId || selectedVehicleId === vr.vehicleId;

        // Build route coordinate sequence: Depot -> Customers -> Depot
        const routeCoords = [];
        const depot = depotMap.get(vr.depotId);
        if (depot) routeCoords.push([depot.latitude, depot.longitude]);

        (vr.customerSequence || []).forEach(cid => {
          const cust = customerMap.get(cid);
          if (cust) routeCoords.push([cust.latitude, cust.longitude]);
        });

        if (depot) routeCoords.push([depot.latitude, depot.longitude]);

        if (routeCoords.length > 1) {
          const polyline = L.polyline(routeCoords, {
            color: vehicleColor,
            weight: isSelected ? 4 : 2,
            opacity: isSelected ? 0.9 : 0.25,
            dashArray: isSelected ? null : '4, 8'
          }).addTo(group);

          polyline.on('click', () => {
            if (onSelectVehicle) onSelectVehicle(vr.vehicleId);
          });

          polyline.bindTooltip(`
            <div style="font-family: var(--font-sans); font-size: 12px;">
              <strong>Vehicle ${vr.vehicleId}</strong><br/>
              Distance: ${vr.totalDistanceKm ? vr.totalDistanceKm.toFixed(1) : '--'} km<br/>
              Time: ${formatTime(vr.totalTravelTimeMinutes)}<br/>
              Demand: ${vr.totalDemand} / ${vr.vehicleCapacity} kg<br/>
              Stops: ${(vr.customerSequence || []).length} customers
            </div>
          `, { sticky: true });
        }
      });
    }

    if (bounds.isValid()) {
      map.fitBounds(bounds, { padding: [40, 40], maxZoom: 14 });
    }
  }, [depots, customers, optimization, selectedVehicleId, onSelectVehicle]);

  return (
    <div className="map-container-card">
      <div className="card-header">
        <h2>🗺️ Interactive Fleet Routing Map</h2>
        <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
          <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
            Routing: <strong>{optimization?.routingProvider || 'REAL GEOGRAPHIC'}</strong>
          </span>
          {selectedVehicleId && (
            <button
              className="btn btn-secondary"
              style={{ padding: '2px 8px', fontSize: '11px' }}
              onClick={() => onSelectVehicle && onSelectVehicle(null)}
            >
              Show All Routes
            </button>
          )}
        </div>
      </div>

      <div ref={mapContainerRef} className="leaflet-map" />

      {/* Map Legend */}
      <div className="map-legend">
        <div className="legend-item">
          <span style={{ fontSize: '14px' }}>🏭</span>
          <span>Depot Hub</span>
        </div>
        <div className="legend-item">
          <span className="legend-color" style={{ backgroundColor: PRIORITY_COLORS.HIGH }}></span>
          <span>High Priority Customer</span>
        </div>
        <div className="legend-item">
          <span className="legend-color" style={{ backgroundColor: PRIORITY_COLORS.MEDIUM }}></span>
          <span>Medium Priority Customer</span>
        </div>
        <div className="legend-item">
          <span className="legend-color" style={{ backgroundColor: PRIORITY_COLORS.LOW }}></span>
          <span>Low Priority Customer</span>
        </div>
      </div>
    </div>
  );
}
