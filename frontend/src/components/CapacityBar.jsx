import React from 'react';

export function CapacityBar({ demand, capacity }) {
  const cap = capacity || 1;
  const dem = demand || 0;
  const percentage = Math.min(100, Math.round((dem / cap) * 100));
  const isOverload = dem > cap;

  let fillColor = 'var(--accent-emerald)';
  if (percentage > 85) fillColor = 'var(--accent-amber)';
  if (isOverload) fillColor = 'var(--accent-rose)';

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '11px', marginBottom: '2px' }}>
        <span style={{ color: 'var(--text-secondary)' }}>Load / Capacity</span>
        <span style={{ fontWeight: '700', color: isOverload ? 'var(--accent-rose)' : 'var(--text-primary)' }}>
          {dem.toFixed(1)} / {cap.toFixed(1)} kg ({percentage}%)
        </span>
      </div>
      <div className="progress-bar-container">
        <div
          className="progress-bar-fill"
          style={{ width: `${percentage}%`, backgroundColor: fillColor }}
        />
      </div>
    </div>
  );
}
