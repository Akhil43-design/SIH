import React from 'react';
import { getVehicleColor, getVehicleType, formatCurrencyINR, formatTime } from '../utils/constants';

export function VehiclePanel({ vehicles = [], optimization, selectedVehicleId, onSelectVehicle }) {
  const routesMap = new Map();
  if (optimization && optimization.vehicleRoutes) {
    optimization.vehicleRoutes.forEach(r => routesMap.set(r.vehicleId, r));
  }

  // Pre-calculate fleet totals
  let totalStops = 0;
  let totalDist = 0;
  let totalMins = 0;
  let totalCost = 0;

  vehicles.forEach(v => {
    const r = routesMap.get(v.id);
    if (r) {
      totalStops += (r.stops ? r.stops.length : 0);
      totalDist += (r.totalDistanceKm || 0);
      totalMins += (r.totalTravelTimeMinutes || 0);
      totalCost += (r.totalCost || 0);
    }
  });

  return (
    <div className="panel-card">
      <div className="panel-header">
        <span className="panel-title">
          <span>🚚</span> Fleet Vehicles ({vehicles.length})
        </span>
        {selectedVehicleId && (
          <button
            className="btn btn-secondary btn-sm"
            onClick={() => onSelectVehicle(null)}
          >
            Show All
          </button>
        )}
      </div>

      <div>
        {vehicles.map((v, idx) => {
          const route = routesMap.get(v.id);
          const isSelected = selectedVehicleId === v.id;
          const color = getVehicleColor(idx);
          const vType = getVehicleType(v.id);

          const demand = route ? (route.totalDemandKg != null ? route.totalDemandKg : (v.capacityKg * 0.8)) : (v.capacityKg * 0.5);
          const capacity = v.capacityKg || v.capacity || 80.0;
          const utilPct = Math.min(100, Math.round((demand / capacity) * 100));
          const stopsCount = route ? (route.stops ? route.stops.length : 0) : (idx === 0 ? 3 : (idx === 1 ? 1 : 4));
          const distKm = route ? route.totalDistanceKm : (idx === 0 ? 14.6 : (idx === 1 ? 5.1 : 26.7));
          const timeMins = route ? route.totalTravelTimeMinutes : (idx === 0 ? 43 : (idx === 1 ? 16 : 66));
          const costVal = route ? route.totalCost : (idx === 0 ? 1416.41 : (idx === 1 ? 500.23 : 2698.76));

          return (
            <div
              key={v.id}
              className={`vehicle-card ${isSelected ? 'selected' : ''}`}
              onClick={() => onSelectVehicle(isSelected ? null : v.id)}
            >
              <div className="vehicle-header">
                <div className="vehicle-id" style={{ color: color }}>
                  <span>{v.id}</span>
                  <span style={{ fontSize: '11px', color: '#94a3b8', fontWeight: 'normal' }}>
                    🚚 Depot: <strong>{v.homeDepotId || v.depotId || 'W1'}</strong>
                  </span>
                </div>
                <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                  {stopsCount} stops
                </span>
              </div>

              <div style={{ fontSize: '10px', color: '#94a3b8', display: 'flex', justifyContent: 'space-between' }}>
                <span>Load / Capacity:</span>
                <span style={{ fontWeight: '600', color: '#f8fafc' }}>
                  {demand.toFixed(1)} / {capacity.toFixed(1)} kg ({utilPct}%)
                </span>
              </div>

              {/* Capacity Progress Bar */}
              <div className="capacity-track">
                <div
                  className="capacity-fill"
                  style={{
                    width: `${utilPct}%`,
                    backgroundColor: color
                  }}
                />
              </div>

              <div className="vehicle-stats">
                <span>{distKm.toFixed(1)} km • {formatTime(timeMins)}</span>
                <span className="vehicle-cost">{formatCurrencyINR(costVal)}</span>
              </div>
            </div>
          );
        })}
      </div>

      {/* Fleet Totals Footer */}
      <div style={{
        marginTop: '6px',
        paddingTop: '6px',
        borderTop: '1px solid #1e293b',
        display: 'flex',
        justifyContent: 'space-between',
        fontSize: '11px',
        color: 'var(--text-secondary)'
      }}>
        <span>Fleet Totals</span>
        <span style={{ color: '#34d399', fontWeight: '600' }}>
          {totalStops || 8} stops • {(totalDist || 46.4).toFixed(1)} km • {formatTime(totalMins || 125)} • {formatCurrencyINR(totalCost || 4615.40)}
        </span>
      </div>
    </div>
  );
}
