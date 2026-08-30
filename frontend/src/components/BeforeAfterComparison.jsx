import React from 'react';
import { formatTime, formatCurrencyINR } from '../utils/constants';

export function BeforeAfterComparison({ before, after }) {
  if (!before || !after) return null;

  return (
    <div style={{ marginTop: '16px', borderTop: '1px solid var(--border-color)', paddingTop: '12px' }}>
      <h4 style={{ fontSize: '13px', fontWeight: '700', marginBottom: '8px', color: '#34d399' }}>
        ✓ Dynamic Fleet Re-Optimization Results (Plan Comparison)
      </h4>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
        {/* Before Box */}
        <div style={{
          background: '#0f172a',
          border: '1px solid #1e293b',
          borderRadius: 'var(--radius-md)',
          padding: '10px',
          fontSize: '11px'
        }}>
          <h4 style={{ color: '#94a3b8', fontSize: '12px', marginBottom: '6px' }}>Plan A (Before Traffic Surge)</h4>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '3px' }}>
            <span style={{ color: 'var(--text-muted)' }}>Run ID:</span>
            <code style={{ color: '#38bdf8' }}>{before.optimizationId || before.id}</code>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '3px' }}>
            <span style={{ color: 'var(--text-muted)' }}>Total Distance:</span>
            <span>{before.totalDistanceKm?.toFixed(1)} km</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '3px' }}>
            <span style={{ color: 'var(--text-muted)' }}>Travel Time:</span>
            <span>{formatTime(before.totalTravelTimeMinutes)}</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '3px' }}>
            <span style={{ color: 'var(--text-muted)' }}>Fuel:</span>
            <span>{before.totalFuelLiters?.toFixed(1)} L</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '3px' }}>
            <span style={{ color: 'var(--text-muted)' }}>Cost:</span>
            <span style={{ color: '#4ade80' }}>{formatCurrencyINR(before.totalCost)}</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <span style={{ color: 'var(--text-muted)' }}>Fitness Score:</span>
            <span style={{ color: '#c084fc' }}>{before.optimizationScore?.toFixed(4) || before.fitnessScore?.toFixed(4)}</span>
          </div>
        </div>

        {/* After Box */}
        <div style={{
          background: '#0f172a',
          border: '1px solid #0284c7',
          borderRadius: 'var(--radius-md)',
          padding: '10px',
          fontSize: '11px'
        }}>
          <h4 style={{ color: '#38bdf8', fontSize: '12px', marginBottom: '6px' }}>Plan B (After Dynamic Re-Opt)</h4>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '3px' }}>
            <span style={{ color: 'var(--text-muted)' }}>Revision ID:</span>
            <code style={{ color: '#34d399' }}>{after.optimizationId || after.id}</code>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '3px' }}>
            <span style={{ color: 'var(--text-muted)' }}>Total Distance:</span>
            <span>{after.totalDistanceKm?.toFixed(1)} km</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '3px' }}>
            <span style={{ color: 'var(--text-muted)' }}>Travel Time:</span>
            <span>{formatTime(after.totalTravelTimeMinutes)}</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '3px' }}>
            <span style={{ color: 'var(--text-muted)' }}>Fuel:</span>
            <span>{after.totalFuelLiters?.toFixed(1)} L</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '3px' }}>
            <span style={{ color: 'var(--text-muted)' }}>Cost:</span>
            <span style={{ color: '#4ade80' }}>{formatCurrencyINR(after.totalCost)}</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <span style={{ color: 'var(--text-muted)' }}>Fitness Score:</span>
            <span style={{ color: '#c084fc' }}>{after.optimizationScore?.toFixed(4) || after.fitnessScore?.toFixed(4)}</span>
          </div>
        </div>
      </div>
    </div>
  );
}
