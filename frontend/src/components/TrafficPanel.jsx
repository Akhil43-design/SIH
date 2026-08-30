import React from 'react';

export function TrafficPanel({ health, onOpenTrafficModal }) {
  return (
    <div className="panel-card">
      <div className="card-header" style={{ margin: '-16px -16px 14px -16px', borderRadius: '12px 12px 0 0' }}>
        <h2>🚦 Intelligent Traffic Model</h2>
        <span className="badge badge-medium">
          {health?.trafficMode || 'SIMULATED'}
        </span>
      </div>

      <div style={{ fontSize: '12px', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '8px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <span>Provider Source:</span>
          <strong>{health?.trafficMode === 'LIVE' ? 'TomTom Live Traffic' : 'Diurnal Congestion Curves'}</strong>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <span>Routing Engine:</span>
          <strong>{health?.routingMode || 'OSRM Real Road Network'}</strong>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <span>Dynamic Rerouting:</span>
          <span style={{ color: 'var(--accent-emerald)', fontWeight: '700' }}>✓ Active & Audited</span>
        </div>
      </div>

      <button
        className="btn btn-cyan"
        style={{ width: '100%', marginTop: '14px', padding: '10px' }}
        onClick={onOpenTrafficModal}
      >
        🚨 Simulate Sudden Congestion Surge
      </button>
    </div>
  );
}
