import React, { useState, useEffect } from 'react';
import { formatISTDateTime, INDIAN_CITIES } from '../utils/constants';

export function Header({
  health,
  userLocation,
  isRefreshing,
  onRefresh,
  onOpenTrafficModal,
  selectedCityId = 'bengaluru',
  onSelectCity
}) {
  const isOnline = health && health.status === 'UP';
  const [currentISTTime, setCurrentISTTime] = useState(formatISTDateTime());

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentISTTime(formatISTDateTime());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const currentCityObj = INDIAN_CITIES.find(c => c.id === selectedCityId) || INDIAN_CITIES[0];

  const locationLabel = userLocation && userLocation.status === 'LIVE'
    ? `${userLocation.latitude.toFixed(4)}° N, ${userLocation.longitude.toFixed(4)}° E`
    : `${currentCityObj.name}, India`;

  return (
    <header className="app-header">
      <div className="brand-section">
        <div className="brand-logo">⚛️</div>
        <div className="brand-titles">
          <h1>QuantumRouteOptimizer</h1>
          <span className="badge-sub">Problem Statement 137 • Quantum-Inspired Multi-Depot Fleet Optimization</span>
        </div>
      </div>

      <div className="header-status-group">
        {/* 1. City Selector Dropdown */}
        <div className="status-pill" style={{ background: '#1e293b', border: '1px solid #38bdf8' }}>
          <span>🇮🇳</span>
          <span style={{ fontWeight: '600', color: '#38bdf8', fontSize: '11px' }}>Operating City:</span>
          <select
            value={selectedCityId}
            onChange={(e) => onSelectCity && onSelectCity(e.target.value)}
            style={{
              background: '#0f172a',
              color: '#f8fafc',
              border: '1px solid #334155',
              borderRadius: '4px',
              padding: '2px 8px',
              fontSize: '11px',
              fontWeight: '600',
              cursor: 'pointer',
              outline: 'none'
            }}
            title="Switch Indian logistics hub region"
          >
            {INDIAN_CITIES.map(c => (
              <option key={c.id} value={c.id}>
                {c.name}, {c.state}
              </option>
            ))}
          </select>
        </div>

        {/* 2. Backend Health */}
        <div className="status-pill">
          <span className={`status-dot ${isOnline ? 'online' : 'offline'}`}></span>
          <span>Backend: {isOnline ? 'ONLINE' : 'OFFLINE'}</span>
          {health && (
            <span style={{ color: 'var(--text-muted)', fontSize: '11px' }}>
              ({health.routingMode} / {health.trafficMode})
            </span>
          )}
        </div>

        {/* 3. My Location GPS Status */}
        <div className="status-pill" title="Current dispatch origin location">
          <span>📍</span>
          <span>Location: {locationLabel}</span>
        </div>

        {/* 4. IST Clock */}
        <div className="status-pill" title="Indian Standard Time">
          <span>🕒</span>
          <span>{currentISTTime}</span>
        </div>

        {/* 5. Simulate Surge */}
        <button
          className="btn btn-red"
          onClick={onOpenTrafficModal}
          title="Simulate sudden traffic congestion surge and trigger dynamic QIGA re-optimization"
        >
          🚨 Simulate Traffic Surge
        </button>

        {/* 6. Refresh */}
        <button
          className="btn btn-primary"
          onClick={onRefresh}
          disabled={isRefreshing}
          title="Refresh fleet telemetry from database"
        >
          {isRefreshing ? '🔄 Refreshing...' : '🔄 Refresh'}
        </button>
      </div>
    </header>
  );
}
