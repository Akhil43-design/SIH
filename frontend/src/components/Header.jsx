import React, { useState, useEffect } from 'react';
import { formatISTDateTime } from '../utils/constants';

export function Header({ health, userLocation, isRefreshing, onRefresh, onOpenTrafficModal }) {
  const isOnline = health && health.status === 'UP';
  const [currentISTTime, setCurrentISTTime] = useState(formatISTDateTime());

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentISTTime(formatISTDateTime());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const locationLabel = userLocation && userLocation.status === 'LIVE'
    ? `${userLocation.latitude.toFixed(4)}° N, ${userLocation.longitude.toFixed(4)}° E`
    : 'Bengaluru, India';

  return (
    <header className="app-header">
      <div className="brand-section">
        <div className="brand-logo">⚛️</div>
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

        <div className="status-pill" title="Current geographic dispatch center">
          <span>📍</span>
          <span>My Location: {locationLabel}</span>
        </div>

        <div className="status-pill" title="Indian Standard Time">
          <span>🕒</span>
          <span>{currentISTTime}</span>
          <span style={{ marginLeft: '4px' }}>🇮🇳</span>
        </div>

        <button
          className="btn btn-red"
          onClick={onOpenTrafficModal}
          title="Simulate sudden traffic congestion and trigger dynamic fleet re-optimization"
        >
          🚨 Simulate Traffic Surge
        </button>

        <button
          className="btn btn-primary"
          onClick={onRefresh}
          disabled={isRefreshing}
          title="Reload fleet entities and optimization results"
        >
          {isRefreshing ? 'Refreshing...' : '🔄 Refresh'}
        </button>
      </div>
    </header>
  );
}
