import React, { useEffect, useRef } from 'react';
import L from 'leaflet';
import { BENGALURU_CENTER, getVehicleColor, PRIORITY_COLORS, formatTime } from '../utils/constants';

export function FleetMap({
  depots,
  customers,
  optimization,
  userLocation,
  selectedVehicleId,
  onSelectVehicle
}) {
  const mapContainerRef = useRef(null);
  const mapInstanceRef = useRef(null);
  const layersGroupRef = useRef(null);

  // Initialize Map centered on Bengaluru
  useEffect(() => {
    if (!mapContainerRef.current) return;

    if (!mapInstanceRef.current) {
      const map = L.map(mapContainerRef.current, {
        center: BENGALURU_CENTER,
        zoom: 11,
        attributionControl: false
      });

      // CartoDB Voyager Map Tiles with rich Indian landmark labels
      L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
        maxZoom: 19,
        subdomains: 'abcd'
      }).addTo(map);

      layersGroupRef.current = L.featureGroup().addTo(map);
      mapInstanceRef.current = map;
    }

    return () => {
      if (mapInstanceRef.current) {
        mapInstanceRef.current.remove();
        mapInstanceRef.current = null;
      }
    };
  }, []);

  // Update Markers, Routes, and Location
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

      const isW1 = d.id === 'W1';
      const depotBg = isW1 ? '#8b5cf6' : '#10b981';
      const labelText = d.name || (isW1 ? 'Peenya, Bengaluru' : 'Hosur Road, Bengaluru');

      const icon = L.divIcon({
        className: '',
        html: `
          <div style="display: flex; flex-direction: column; align-items: center; cursor: pointer;">
            <div style="background: ${depotBg}; color: white; border: 2px solid white; border-radius: 6px; padding: 2px 8px; font-size: 11px; font-weight: 700; box-shadow: 0 4px 12px rgba(0,0,0,0.5); white-space: nowrap;">
              Depot ${d.id}
            </div>
            <div style="font-size: 9px; color: #1e293b; background: rgba(255,255,255,0.9); padding: 1px 4px; border-radius: 3px; margin-top: 1px; font-weight: 600; white-space: nowrap;">
              ${labelText}
            </div>
          </div>
        `,
        iconSize: [100, 40],
        iconAnchor: [50, 20]
      });

      const marker = L.marker(latLng, { icon }).addTo(group);
      marker.bindPopup(`
        <div style="font-family: var(--font-sans); font-size: 12px; line-height: 1.4;">
          <strong style="color: ${depotBg}; font-size: 13px;">Depot ${d.id}: ${d.name || ''}</strong><br/>
          <span style="color: #64748b;">City: Bengaluru, Karnataka</span><br/>
          <span>Coordinates: [${d.latitude.toFixed(4)}, ${d.longitude.toFixed(4)}]</span>
        </div>
      `);
    });

    // 2. Draw Customers
    (customers || []).forEach((c, idx) => {
      if (!c.latitude || !c.longitude) return;
      const latLng = [c.latitude, c.longitude];
      bounds.extend(latLng);

      const color = PRIORITY_COLORS[c.priority] || '#3b82f6';
      const isCancelled = !!c.cancelled;
      const displayNum = (idx + 1);

      const icon = L.divIcon({
        className: '',
        html: `
          <div class="custom-customer-pin" style="width: 24px; height: 24px; background-color: ${isCancelled ? '#64748b' : color}; ${isCancelled ? 'opacity: 0.5; text-decoration: line-through;' : ''}">
            ${displayNum}
          </div>
        `,
        iconSize: [24, 24],
        iconAnchor: [12, 12]
      });

      const marker = L.marker(latLng, { icon }).addTo(group);
      marker.bindPopup(`
        <div style="font-family: var(--font-sans); font-size: 12px; line-height: 1.4;">
          <strong style="color: ${color}; font-size: 13px;">#${displayNum}: ${c.name || c.id}</strong><br/>
          <span style="color: #64748b;">Priority: <strong>${c.priority}</strong></span><br/>
          <span>Demand: <strong>${c.demandKg != null ? c.demandKg : c.demand} kg</strong></span><br/>
          <span>Time Window: [${formatTime(c.earliestTimeMinutes || 30)} - ${formatTime(c.latestTimeMinutes || 180)}]</span><br/>
          <span>Coordinates: [${c.latitude.toFixed(4)}, ${c.longitude.toFixed(4)}]</span>
        </div>
      `);
    });

    // 3. Draw Optimization Vehicle Routes
    if (optimization && optimization.vehicleRoutes) {
      optimization.vehicleRoutes.forEach((vr, vIdx) => {
        const isSelected = !selectedVehicleId || selectedVehicleId === vr.vehicleId;
        const color = getVehicleColor(vIdx);

        const homeDepot = depotMap.get(vr.homeDepotId) || depots[0];
        if (!homeDepot) return;

        const routeCoords = [[homeDepot.latitude, homeDepot.longitude]];
        (vr.stops || []).forEach(stop => {
          const cust = customerMap.get(stop.customerId);
          if (cust && cust.latitude && cust.longitude) {
            routeCoords.push([cust.latitude, cust.longitude]);
          }
        });
        routeCoords.push([homeDepot.latitude, homeDepot.longitude]);

        // Draw Polyline
        const polyline = L.polyline(routeCoords, {
          color: color,
          weight: isSelected ? 4 : 2,
          opacity: isSelected ? 0.9 : 0.25,
          lineJoin: 'round',
          dashArray: isSelected ? null : '4, 8'
        }).addTo(group);

        polyline.on('click', () => {
          if (onSelectVehicle) onSelectVehicle(vr.vehicleId);
        });

        polyline.bindTooltip(`
          <strong>Vehicle ${vr.vehicleId}</strong> (${vr.stops ? vr.stops.length : 0} stops)<br/>
          Distance: ${vr.totalDistanceKm ? vr.totalDistanceKm.toFixed(1) : '--'} km
        `, { sticky: true });

        // Add truck marker along route center
        if (routeCoords.length >= 2) {
          const midIdx = Math.floor(routeCoords.length / 2);
          const midCoord = routeCoords[midIdx];

          const truckIcon = L.divIcon({
            className: '',
            html: `<div style="font-size: 16px; transform: scaleX(-1); text-shadow: 0 0 6px ${color};">🚚</div>`,
            iconSize: [20, 20],
            iconAnchor: [10, 10]
          });
          L.marker(midCoord, { icon: truckIcon }).addTo(group);
        }
      });
    }

    // 4. Draw User's Real Location Marker
    if (userLocation && userLocation.latitude && userLocation.longitude) {
      const userLatLng = [userLocation.latitude, userLocation.longitude];
      bounds.extend(userLatLng);

      const locIcon = L.divIcon({
        className: '',
        html: `
          <div style="display: flex; flex-direction: column; align-items: center;">
            <div class="my-location-pulse">
              <div class="my-location-ring"></div>
              <div class="my-location-dot"></div>
            </div>
            <div style="background: #2563eb; color: white; font-size: 10px; font-weight: 700; padding: 1px 6px; border-radius: 4px; box-shadow: 0 2px 6px rgba(0,0,0,0.5); white-space: nowrap; margin-top: 2px;">
              My Location
            </div>
          </div>
        `,
        iconSize: [70, 45],
        iconAnchor: [35, 20]
      });

      const userMarker = L.marker(userLatLng, { icon: locIcon }).addTo(group);
      userMarker.bindPopup(`
        <div style="font-family: var(--font-sans); font-size: 12px;">
          <strong style="color: #38bdf8;">📍 Your Current Location</strong><br/>
          <span>${userLocation.cityName || 'Bengaluru, India'}</span><br/>
          <span>Coordinates: [${userLocation.latitude.toFixed(4)}, ${userLocation.longitude.toFixed(4)}]</span>
          ${userLocation.accuracy ? `<br/><span style="color: #64748b;">Accuracy: ±${Math.round(userLocation.accuracy)}m</span>` : ''}
        </div>
      `);
    }

    // Fit bounds if we have points
    if (bounds.isValid()) {
      map.fitBounds(bounds, { padding: [40, 40], maxZoom: 13 });
    }
  }, [depots, customers, optimization, userLocation, selectedVehicleId]);

  // Google Maps link from first depot or center
  const gmapUrl = depots && depots.length > 0
    ? `https://www.google.com/maps/search/?api=1&query=${depots[0].latitude},${depots[0].longitude}`
    : `https://www.google.com/maps/search/?api=1&query=${BENGALURU_CENTER[0]},${BENGALURU_CENTER[1]}`;

  return (
    <div className="map-wrapper">
      {/* OSRM Routing Banner */}
      <div className="map-routing-banner">
        Routing: <span>OSRMRoutingProvider</span> [https://router.project-osrm.org]
      </div>

      <div ref={mapContainerRef} className="map-container" />

      {/* Map Legend */}
      <div className="map-legend">
        <div className="legend-item">
          <span>🏭</span> <span>Depot Hub</span>
        </div>
        <div className="legend-item">
          <span className="legend-dot" style={{ backgroundColor: '#ef4444' }}></span>
          <span>High Priority Customer</span>
        </div>
        <div className="legend-item">
          <span className="legend-dot" style={{ backgroundColor: '#f59e0b' }}></span>
          <span>Medium Priority Customer</span>
        </div>
        <div className="legend-item">
          <span className="legend-dot" style={{ backgroundColor: '#10b981' }}></span>
          <span>Low Priority Customer</span>
        </div>
        <div className="legend-item">
          <span style={{ color: '#8b5cf6', fontWeight: 'bold' }}>━</span>
          <span>Vehicle Route</span>
        </div>
        <div className="legend-item">
          <span>🔘</span> <span>My Location</span>
        </div>
      </div>

      {/* Google Maps Link */}
      <a
        href={gmapUrl}
        target="_blank"
        rel="noopener noreferrer"
        className="map-external-link"
        title="Open coordinates in Google Maps"
      >
        <span>📍</span> Open in Google Maps
      </a>
    </div>
  );
}
