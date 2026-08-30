import React from 'react';

export function TrafficPanel({ health, trafficSnapshot, onOpenTrafficModal }) {
  const isLive = trafficSnapshot && trafficSnapshot.isLive;
  const providerLabel = isLive ? 'TomTom Flow Segment API' : 'Diurnal Congestion Curves';
  const routingEngine = (health && health.routingMode) ? health.routingMode : 'REAL_OSRM';

  return (
    <div className="panel-card">
      <div className="panel-header">
        <span className="panel-title">
          <span>🚦</span> Intelligent Traffic Model
        </span>
        <span className={`badge-tag ${isLive ? 'live' : 'simulated'}`}>
          {isLive ? 'LIVE' : 'SIMULATED'}
        </span>
      </div>

      <div style={{ fontSize: '11px', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '4px', marginBottom: '10px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <span style={{ color: 'var(--text-muted)' }}>Provider Source:</span>
          <span style={{ color: '#f8fafc', fontWeight: '500' }}>{providerLabel}</span>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <span style={{ color: 'var(--text-muted)' }}>Routing Engine:</span>
          <span style={{ color: '#38bdf8', fontFamily: 'var(--font-mono)' }}>{routingEngine}</span>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <span style={{ color: 'var(--text-muted)' }}>Dynamic Rerouting:</span>
          <span style={{ color: '#34d399', fontWeight: '600' }}>✓ Active & Audited</span>
        </div>
      </div>

      <button
        className="btn btn-red"
        style={{ width: '100%' }}
        onClick={onOpenTrafficModal}
        title="Inject traffic congestion and re-evaluate routes in real time"
      >
        🚨 Simulate Sudden Congestion Surge
      </button>
    </div>
  );
}
