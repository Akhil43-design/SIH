import React from 'react';
import { formatTime } from '../utils/constants';

export function KpiMetrics({ optimization, totalVehicles, totalCustomers, totalDepots }) {
  const hasResult = !!optimization && optimization.status === 'COMPLETED';

  return (
    <div className="kpi-grid">
      {/* Total Distance */}
      <div className="kpi-card">
        <div className="kpi-label">
          <span>Total Distance</span>
          <span>🛣️</span>
        </div>
        <div className="kpi-value">
          {hasResult ? `${optimization.totalDistanceKm.toFixed(1)} km` : '--'}
        </div>
        <div className="kpi-sub">
          Across {hasResult ? (optimization.vehicleRoutes ? optimization.vehicleRoutes.length : 0) : totalVehicles} active routes
        </div>
      </div>

      {/* Total Travel Time */}
      <div className="kpi-card">
        <div className="kpi-label">
          <span>Travel Time</span>
          <span>⏱️</span>
        </div>
        <div className="kpi-value">
          {hasResult ? formatTime(optimization.totalTravelTimeMinutes) : '--'}
        </div>
        <div className="kpi-sub">
          Traffic adjusted ({hasResult ? `${optimization.totalTravelTimeMinutes.toFixed(1)} min` : '--'})
        </div>
      </div>

      {/* Total Fuel */}
      <div className="kpi-card">
        <div className="kpi-label">
          <span>Fuel Consumption</span>
          <span>⛽</span>
        </div>
        <div className="kpi-value">
          {hasResult ? `${optimization.totalFuelLiters.toFixed(1)} L` : '--'}
        </div>
        <div className="kpi-sub">Fleet total calculated</div>
      </div>

      {/* Total Transportation Cost */}
      <div className="kpi-card">
        <div className="kpi-label">
          <span>Total Cost</span>
          <span>💰</span>
        </div>
        <div className="kpi-value">
          {hasResult ? `$${optimization.totalCost.toFixed(2)}` : '--'}
        </div>
        <div className="kpi-sub">Distance + Fuel + Operations</div>
      </div>

      {/* Optimization Score */}
      <div className="kpi-card">
        <div className="kpi-label">
          <span>QIGA Score</span>
          <span>⚛️</span>
        </div>
        <div className="kpi-value">
          {hasResult ? optimization.optimizationScore.toFixed(4) : '--'}
        </div>
        <div className="kpi-sub">
          {hasResult ? `Runtime: ${optimization.runtimeMs || '<1'}ms` : 'Ready to optimize'}
        </div>
      </div>

      {/* Fleet Summary / Constraint Status */}
      <div className="kpi-card">
        <div className="kpi-label">
          <span>Constraints</span>
          <span>🛡️</span>
        </div>
        <div className="kpi-value" style={{ fontSize: '18px' }}>
          {hasResult ? (
            optimization.totalCapacityViolations === 0 && optimization.totalTimeViolations === 0 ? (
              <span style={{ color: 'var(--accent-emerald)' }}>✓ 100% Valid</span>
            ) : (
              <span style={{ color: 'var(--accent-rose)' }}>
                {optimization.totalCapacityViolations + optimization.totalTimeViolations} Violations
              </span>
            )
          ) : (
            `${totalVehicles}V / ${totalCustomers}C / ${totalDepots}D`
          )}
        </div>
        <div className="kpi-sub">
          {hasResult ? `${optimization.unassignedCount || 0} unassigned` : 'Capacity & Time Windows'}
        </div>
      </div>
    </div>
  );
}
