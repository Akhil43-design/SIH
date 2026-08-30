import React from 'react';
import { CapacityBar } from './CapacityBar';
import { formatTime, getVehicleColor } from '../utils/constants';

export function VehiclePanel({ vehicles, optimization, selectedVehicleId, onSelectVehicle }) {
  // Map vehicle routes from latest optimization
  const routeMap = new Map();
  if (optimization && optimization.vehicleRoutes) {
    optimization.vehicleRoutes.forEach((vr) => {
      routeMap.set(vr.vehicleId, vr);
    });
  }

  return (
    <div className="table-responsive">
      <table className="data-table">
        <thead>
          <tr>
            <th>Vehicle</th>
            <th>Depot</th>
            <th>Capacity & Load</th>
            <th>Stops</th>
            <th>Distance / Time</th>
            <th>Cost</th>
          </tr>
        </thead>
        <tbody>
          {(vehicles || []).map((v, idx) => {
            const vr = routeMap.get(v.id);
            const isSelected = selectedVehicleId === v.id;
            const color = getVehicleColor(idx);

            return (
              <tr
                key={v.id}
                onClick={() => onSelectVehicle && onSelectVehicle(isSelected ? null : v.id)}
                style={{
                  cursor: 'pointer',
                  backgroundColor: isSelected ? 'rgba(99, 102, 241, 0.15)' : undefined
                }}
              >
                <td>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <span style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: color }}></span>
                    <strong>{v.id}</strong>
                  </div>
                </td>
                <td>
                  <code>{v.depotId || '--'}</code>
                </td>
                <td style={{ minWidth: '130px' }}>
                  <CapacityBar
                    demand={vr ? vr.totalDemand : 0}
                    capacity={v.capacity}
                  />
                </td>
                <td>
                  {vr ? (
                    <span style={{ fontSize: '11px', fontFamily: 'var(--font-mono)' }}>
                      {(vr.customerSequence || []).length} stops
                    </span>
                  ) : (
                    <span style={{ color: 'var(--text-muted)' }}>Idle</span>
                  )}
                </td>
                <td>
                  {vr ? (
                    <span style={{ fontSize: '11px' }}>
                      {vr.totalDistanceKm?.toFixed(1)} km • {formatTime(vr.totalTravelTimeMinutes)}
                    </span>
                  ) : (
                    '--'
                  )}
                </td>
                <td>
                  {vr ? (
                    <strong style={{ color: 'var(--accent-emerald)' }}>
                      ${vr.totalCost?.toFixed(2)}
                    </strong>
                  ) : (
                    '--'
                  )}
                </td>
              </tr>
            );
          })}
          {(!vehicles || vehicles.length === 0) && (
            <tr>
              <td colSpan={6} style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '20px' }}>
                No active fleet vehicles configured.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
