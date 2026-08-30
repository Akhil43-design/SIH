import React, { useEffect, useRef, useState } from 'react';
import L from 'leaflet';
import { BENGALURU_CENTER, getVehicleColor, PRIORITY_COLORS, formatTime } from '../utils/constants';
import { fetchOSRMRouteGeometry } from '../services/api';

export function FleetMap({
  depots = [],
  customers = [],
  optimization,
  userLocation,
  selectedVehicleId,
  onSelectVehicle
}) {
  const mapContainerRef = useRef(null);
  const mapInstanceRef = useRef(null);
  const layersGroupRef = useRef(null);

  const [isLoadingGeometry, setIsLoadingGeometry] = useState(false);
  const [roadGeometries, setRoadGeometries] = useState({});

  // 1. Initialize Map centered on Bengaluru
  useEffect(() => {
    if (!mapContainerRef.current) return;

    let map = mapInstanceRef.current;
    if (!map) {
      try {
        map = L.map(mapContainerRef.current, {
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
      } catch (e) {
        console.warn('Map initialization:', e);
      }
    }

    return () => {
      if (mapInstanceRef.current) {
        try {
          mapInstanceRef.current.remove();
        } catch (e) {
          // ignore
        }
        mapInstanceRef.current = null;
      }
    };
  }, []);

  // 2. Fetch OSRM Road-Following Geometries for all Vehicle Routes
  useEffect(() => {
    const depotMap = new Map();
    (depots || []).forEach(d => {
      if (d && d.latitude && d.longitude) {
        depotMap.set(d.id, d);
        if (d.depotId) depotMap.set(d.depotId, d);
      }
    });

    const customerMap = new Map();
    (customers || []).forEach(c => {
      if (c && c.latitude && c.longitude) {
        customerMap.set(c.id, c);
        if (c.customerId) customerMap.set(c.customerId, c);
      }
    });

    let routesToFetch = [];
    if (optimization && optimization.vehicleRoutes && Array.isArray(optimization.vehicleRoutes)) {
      routesToFetch = optimization.vehicleRoutes;
    } else {
      // Default initial Indian demo routes for visual demo
      routesToFetch = [
        { vehicleId: 'V1', homeDepotId: 'W1', customerSequence: ['C6', 'C3'] },
        { vehicleId: 'V2', homeDepotId: 'W1', customerSequence: ['C1', 'C2'] },
        { vehicleId: 'V3', homeDepotId: 'W2', customerSequence: ['C4', 'C5', 'C7', 'C8'] }
      ];
    }

    let isMounted = true;
    const loadAllGeometries = async () => {
      setIsLoadingGeometry(true);
      const newGeometries = {};

      for (const vr of routesToFetch) {
        const depotId = vr.homeDepotId || vr.depotId || 'W1';
        const homeDepot = depotMap.get(depotId) || (depots.length > 0 ? depots[0] : null);
        if (!homeDepot || !homeDepot.latitude) continue;

        const waypoints = [[homeDepot.latitude, homeDepot.longitude]];
        const stopIds = vr.customerSequence || (vr.stops ? vr.stops.map(s => s.customerId || s.id) : []);
        stopIds.forEach(cId => {
          const cust = customerMap.get(cId);
          if (cust && cust.latitude && cust.longitude) {
            waypoints.push([cust.latitude, cust.longitude]);
          }
        });
        waypoints.push([homeDepot.latitude, homeDepot.longitude]);

        if (waypoints.length >= 2) {
          try {
            const roadCoords = await fetchOSRMRouteGeometry(waypoints);
            newGeometries[vr.vehicleId] = roadCoords;
          } catch (e) {
            newGeometries[vr.vehicleId] = waypoints;
          }
        }
      }

      if (isMounted) {
        setRoadGeometries(newGeometries);
        setIsLoadingGeometry(false);
      }
    };

    loadAllGeometries();

    return () => {
      isMounted = false;
    };
  }, [optimization, depots, customers]);

  // 3. Render Markers, Road-Following Polylines, and Vehicle Position Icons
  useEffect(() => {
    const map = mapInstanceRef.current;
    const group = layersGroupRef.current;
    if (!map || !group) return;

    try {
      group.clearLayers();
    } catch (e) {
      return;
    }

    const bounds = L.latLngBounds([]);

    // 1. Draw Depots
    (depots || []).forEach(d => {
      if (!d || !d.latitude || !d.longitude) return;
      const latLng = [d.latitude, d.longitude];
      bounds.extend(latLng);

      const isW1 = d.id === 'W1';
      const depotBg = isW1 ? '#8b5cf6' : '#10b981';
      const labelText = d.name || (isW1 ? 'Peenya Industrial Area' : 'Hosur Road Logistics Hub');

      const icon = L.divIcon({
        className: 'custom-leaflet-div-icon',
        html: `
          <div style="display: flex; flex-direction: column; align-items: center; cursor: pointer;">
            <div style="background: ${depotBg}; color: white; border: 2px solid white; border-radius: 6px; padding: 2px 8px; font-size: 11px; font-weight: 700; box-shadow: 0 4px 12px rgba(0,0,0,0.5); white-space: nowrap;">
              Depot ${d.id}
            </div>
            <div style="font-size: 9px; color: #1e293b; background: rgba(255,255,255,0.95); padding: 1px 5px; border-radius: 3px; margin-top: 1px; font-weight: 600; white-space: nowrap;">
              ${labelText}
            </div>
          </div>
        `,
        iconSize: [110, 42],
        iconAnchor: [55, 21]
      });

      const marker = L.marker(latLng, { icon }).addTo(group);
      marker.bindPopup(`
        <div style="font-family: var(--font-sans); font-size: 12px; line-height: 1.4;">
          <strong style="color: ${depotBg}; font-size: 13px;">Depot ${d.id}: ${d.name || ''}</strong><br/>
          <span style="color: #64748b;">Region: Bengaluru, Karnataka</span><br/>
          <span>Coordinates: [${d.latitude.toFixed(4)}, ${d.longitude.toFixed(4)}]</span>
        </div>
      `);
    });

    // 2. Draw Customer Destination Pins
    (customers || []).forEach((c, idx) => {
      if (!c || !c.latitude || !c.longitude) return;
      const latLng = [c.latitude, c.longitude];
      bounds.extend(latLng);

      const color = PRIORITY_COLORS[c.priority] || '#3b82f6';
      const isCancelled = !!c.cancelled;
      const displayNum = (idx + 1);

      const icon = L.divIcon({
        className: 'custom-leaflet-div-icon',
        html: `
          <div style="display: flex; flex-direction: column; align-items: center; cursor: pointer;">
            <div class="custom-customer-pin" style="width: 24px; height: 24px; background-color: ${isCancelled ? '#64748b' : color}; ${isCancelled ? 'opacity: 0.5; text-decoration: line-through;' : ''}">
              ${displayNum}
            </div>
            <div style="font-size: 9px; color: #0f172a; background: rgba(255,255,255,0.92); padding: 1px 4px; border-radius: 3px; margin-top: 1px; font-weight: 600; white-space: nowrap; max-width: 90px; overflow: hidden; text-overflow: ellipsis;">
              ${c.name ? c.name.split(',')[0] : `Dest ${displayNum}`}
            </div>
          </div>
        `,
        iconSize: [90, 42],
        iconAnchor: [45, 12]
      });

      const marker = L.marker(latLng, { icon }).addTo(group);
      marker.bindPopup(`
        <div style="font-family: var(--font-sans); font-size: 12px; line-height: 1.4;">
          <strong style="color: ${color}; font-size: 13px;">#${displayNum}: ${c.name || c.id}</strong><br/>
          <span style="color: #64748b;">Priority: <strong>${c.priority || 'MEDIUM'}</strong></span><br/>
          <span>Demand: <strong>${c.demandKg != null ? c.demandKg : (c.demand || 20.0)} kg</strong></span><br/>
          <span>Coordinates: [${c.latitude.toFixed(4)}, ${c.longitude.toFixed(4)}]</span>
        </div>
      `);
    });

    // 3. Draw Actual OSRM Road-Following Polylines & Moving Truck Markers
    const vehicleKeys = Object.keys(roadGeometries);
    vehicleKeys.forEach((vId, vIdx) => {
      const roadCoords = roadGeometries[vId];
      if (!roadCoords || roadCoords.length < 2) return;

      const isSelected = !selectedVehicleId || selectedVehicleId === vId;
      const color = getVehicleColor(vIdx);

      // Extend bounds with road points
      roadCoords.forEach(pt => bounds.extend(pt));

      // Draw real road polyline
      const polyline = L.polyline(roadCoords, {
        color: color,
        weight: isSelected ? 4 : 2,
        opacity: isSelected ? 0.95 : 0.2,
        lineJoin: 'round',
        smoothFactor: 1.0,
        dashArray: isSelected ? null : '4, 8'
      }).addTo(group);

      polyline.on('click', () => {
        if (onSelectVehicle) onSelectVehicle(vId);
      });

      polyline.bindTooltip(`
        <strong>Vehicle ${vId}</strong> (OSRM Road-Following Route)<br/>
        Road Points: ${roadCoords.length} curve segments
      `, { sticky: true });

      // Place moving truck marker at midpoint along the actual road path
      if (roadCoords.length >= 2) {
        const midIdx = Math.floor(roadCoords.length / 2);
        const midPoint = roadCoords[midIdx];

        const truckIcon = L.divIcon({
          className: 'custom-leaflet-div-icon',
          html: `<div style="font-size: 17px; transform: scaleX(-1); filter: drop-shadow(0 0 4px ${color});">🚚</div>`,
          iconSize: [22, 22],
          iconAnchor: [11, 11]
        });
        L.marker(midPoint, { icon: truckIcon }).addTo(group);
      }
    });

    // 4. Draw User's Real GPS Location Marker
    if (userLocation && userLocation.latitude && userLocation.longitude) {
      const userLatLng = [userLocation.latitude, userLocation.longitude];
      bounds.extend(userLatLng);

      const locIcon = L.divIcon({
        className: 'custom-leaflet-div-icon',
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

    // Fit bounds safely
    try {
      if (bounds.isValid() && bounds.getNorthEast() && bounds.getSouthWest()) {
        map.fitBounds(bounds, { padding: [35, 35], maxZoom: 13 });
      }
    } catch (e) {
      // ignore
    }
  }, [depots, customers, roadGeometries, userLocation, selectedVehicleId, onSelectVehicle]);

  const gmapUrl = (depots && depots.length > 0 && depots[0].latitude)
    ? `https://www.google.com/maps/search/?api=1&query=${depots[0].latitude},${depots[0].longitude}`
    : `https://www.google.com/maps/search/?api=1&query=${BENGALURU_CENTER[0]},${BENGALURU_CENTER[1]}`;

  return (
    <div className="map-wrapper">
      {/* OSRM Routing Banner & Status */}
      <div className="map-routing-banner">
        Routing: <span>OSRMRoutingProvider</span> [https://router.project-osrm.org]
        {isLoadingGeometry && (
          <span style={{ color: '#fbbf24', marginLeft: '8px' }}>
            ⚡ Fetching real OSRM road geometry...
          </span>
        )}
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
          <span>Vehicle Route (V1)</span>
        </div>
        <div className="legend-item">
          <span style={{ color: '#3b82f6', fontWeight: 'bold' }}>━</span>
          <span>Vehicle Route (V2)</span>
        </div>
        <div className="legend-item">
          <span style={{ color: '#10b981', fontWeight: 'bold' }}>━</span>
          <span>Vehicle Route (V3)</span>
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
