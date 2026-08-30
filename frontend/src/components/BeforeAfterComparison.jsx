import React from 'react';
import { formatTime } from '../utils/constants';

export function BeforeAfterComparison({ before, after }) {
  if (!before || !after) return null;

  return (
    <div style={{ marginTop: '20px', borderTop: '1px solid var(--border-color)', paddingTop: '16px' }}>
      <h4 style={{ fontSize: '13px', fontWeight: '700', marginBottom: '8px', color: 'var(--accent-emerald)' }}>
        ✓ Dynamic Fleet Re-Optimization Results
      </h4>

      <div className="comparison-grid">
        {/* Before Box */}
        <div className="comparison-box">
          <h4>Plan A (Before Traffic)</h4>
          <div className="comparison-row">
            <span>Run ID</span>
            <code>{before.optimizationId}</code>
          </div>
          <div className="comparison-row">
            <span>Total Distance</span>
            <span>{before.totalDistanceKm?.toFixed(1)} km</span>
          </div>
          <div className="comparison-row">
            <span>Travel Time</span>
            <span>{formatTime(before.totalTravelTimeMinutes)}</span>
          </div>
          <div className="comparison-row">
            <span>Fuel Consumption</span>
            <span>{before.totalFuelLiters?.toFixed(1)} L</span>
          </div>
          <div className="comparison-row">
            <span>Cost</span>
            <span>${before.totalCost?.toFixed(2)}</span>
          </div>
          <div className="comparison-row">
            <span>Score (Fitness)</span>
            <span>{before.optimizationScore?.toFixed(4)}</span>
          </div>
        </div>

        {/* After Box */}
        <div className="comparison-box" style={{ borderColor: 'var(--accent-cyan)' }}>
          <h4 style={{ color: 'var(--accent-cyan)' }}>Plan B (After Dynamic Re-Opt)</h4>
          <div className="comparison-row">
            <span>Revision ID</span>
            <code>{after.optimizationId}</code>
          </div>
          <div className="comparison-row">
            <span>Total Distance</span>
            <span className={after.totalDistanceKm <= before.totalDistanceKm ? 'val-improved' : 'val-degraded'}>
              {after.totalDistanceKm?.toFixed(1)} km
            </span>
          </div>
          <div className="comparison-row">
            <span>Travel Time</span>
            <span className={after.totalTravelTimeMinutes <= before.totalTravelTimeMinutes ? 'val-improved' : 'val-degraded'}>
              {formatTime(after.totalTravelTimeMinutes)}
            </span>
          </div>
          <div className="comparison-row">
            <span>Fuel Consumption</span>
            <span>{after.totalFuelLiters?.toFixed(1)} L</span>
          </div>
          <div className="comparison-row">
            <span>Cost</span>
            <span>${after.totalCost?.toFixed(2)}</span>
          </div>
          <div className="comparison-row">
            <span>Score (Fitness)</span>
            <span className={after.optimizationScore >= before.optimizationScore ? 'val-improved' : 'val-degraded'}>
              {after.optimizationScore?.toFixed(4)}
            </span>
          </div>
        </div>
      </div>

      {/* Revised Route Sequence Details */}
      <div style={{ marginTop: '10px', fontSize: '11px', color: 'var(--text-secondary)' }}>
        <strong>Reassigned Vehicle Stop Sequences:</strong>
        {(after.vehicleRoutes || []).map((vr) => (
          <div key={vr.vehicleId} style={{ marginTop: '4px', fontFamily: 'var(--font-mono)' }}>
            <strong>{vr.vehicleId}:</strong> {vr.fullRouteLocationIds ? vr.fullRouteLocationIds.join(' → ') : vr.customerSequence.join(' → ')}
          </div>
        ))}
      </div>
    </div>
  );
}
