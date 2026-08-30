import React from 'react';

export function Header({ health, isRefreshing, onRefresh, onOpenTrafficModal }) {
  const isOnline = health && health.status === 'UP';

  return (
    <header className="app-header">
      <div className="brand-section">
        <div className="brand-logo">⚡</div>
        <div className="brand-titles">
          <h1>QuantumRouteOptimizer</h1>
          <span className="badge-sub">Problem Statement 137 • Multi-Depot Fleet Optimization</span>
        </div>
      </div>

      <div className="header-status-group">
        <div className="status-pill">
          <span className={`status-dot ${isOnline ? 'online' : 'offline'}`}></span>
          <span>Backend: {isOnline ? 'ONLINE' : 'OFFLINE'}</span>
          {health && (
            <span style={{ color: 'var(--text-muted)', fontSize: '11px' }}>
              ({health.routingMode} / {health.trafficMode})
            </span>
          )}
        </div>

        <button
          className="btn btn-cyan"
          onClick={onOpenTrafficModal}
          title="Simulate congestion and trigger dynamic re-optimization"
        >
          🚨 Simulate Traffic Surge
        </button>

        <button
          className="btn btn-secondary"
          onClick={onRefresh}
          disabled={isRefreshing}
          title="Reload customers, vehicles, depots, and latest optimization"
        >
          {isRefreshing ? 'Refreshing...' : '🔄 Refresh'}
        </button>
      </div>
    </header>
  );
}
