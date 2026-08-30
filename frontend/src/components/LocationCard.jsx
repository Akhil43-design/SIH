import React from 'react';

export function LocationCard({
  userLocation,
  nearestDepot,
  distanceToDepotKm,
  useLocationAsOrigin,
  onToggleUseAsOrigin,
  onRequestLocation,
  isLocating
}) {
  const isLive = userLocation && userLocation.status === 'LIVE';
  const isPermissionRequired = !userLocation || userLocation.status === 'PERMISSION_REQUIRED';
  const isUnavailable = userLocation && userLocation.status === 'UNAVAILABLE';

  const statusLabel = isLive ? 'LIVE' : (isPermissionRequired ? 'PERMISSION REQUIRED' : 'UNAVAILABLE');
  const statusClass = isLive ? 'completed' : (isPermissionRequired ? 'simulated' : 'failed');

  return (
    <div className="panel-card">
      <div className="panel-header">
        <span className="panel-title">
          <span>📍</span> My Location
        </span>
        <span className={`badge-tag ${statusClass}`}>
          ● {statusLabel}
        </span>
      </div>

      <div style={{ fontSize: '12px', color: 'var(--text-secondary)', marginBottom: '8px' }}>
        {isLive ? (
          <div>
            <div style={{ fontFamily: 'var(--font-mono)', fontWeight: '600', color: '#38bdf8' }}>
              {userLocation.latitude.toFixed(4)}° N, {userLocation.longitude.toFixed(4)}° E
            </div>
            <div style={{ fontSize: '11px', color: 'var(--text-muted)', marginTop: '2px' }}>
              {userLocation.cityName || 'Bengaluru, Karnataka, India'}
            </div>
            {userLocation.accuracy && (
              <div style={{ fontSize: '10px', color: '#94a3b8', marginTop: '2px' }}>
                Accuracy: ±{Math.round(userLocation.accuracy)} m
              </div>
            )}
          </div>
        ) : (
          <div>
            <div style={{ fontFamily: 'var(--font-mono)', color: '#94a3b8' }}>
              12.9716° N, 77.5946° E
            </div>
            <div style={{ fontSize: '11px', color: 'var(--text-muted)', marginTop: '2px' }}>
              Bengaluru, Karnataka, India (Demo Center)
            </div>
            {userLocation && userLocation.errorMessage && (
              <div style={{ fontSize: '10px', color: '#f87171', marginTop: '2px' }}>
                {userLocation.errorMessage}
              </div>
            )}
          </div>
        )}
      </div>

      <div style={{ display: 'flex', gap: '6px', marginBottom: '8px' }}>
        <button
          className="btn btn-cyan"
          style={{ flex: 1 }}
          onClick={onRequestLocation}
          disabled={isLocating}
          title="Detect GPS location from your browser"
        >
          {isLocating ? 'Detecting...' : (isLive ? '🔄 Refresh Location' : '📍 Use My Location')}
        </button>
      </div>

      {nearestDepot && (
        <div style={{
          background: 'rgba(15, 23, 42, 0.6)',
          border: '1px solid #1e293b',
          borderRadius: 'var(--radius-sm)',
          padding: '6px 8px',
          fontSize: '11px'
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--text-muted)' }}>
            <span>Nearest Depot:</span>
            <span style={{ color: '#c084fc', fontWeight: '600' }}>{nearestDepot.name || nearestDepot.id}</span>
          </div>
          {distanceToDepotKm != null && (
            <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--text-muted)', marginTop: '2px' }}>
              <span>Distance to Depot:</span>
              <span style={{ color: '#38bdf8', fontWeight: '600' }}>{distanceToDepotKm.toFixed(2)} km</span>
            </div>
          )}
        </div>
      )}

      <div style={{ marginTop: '8px', display: 'flex', alignItems: 'center', gap: '6px', fontSize: '11px' }}>
        <input
          type="checkbox"
          id="chkUseAsOrigin"
          checked={useLocationAsOrigin}
          onChange={(e) => onToggleUseAsOrigin(e.target.checked)}
          style={{ cursor: 'pointer' }}
        />
        <label htmlFor="chkUseAsOrigin" style={{ cursor: 'pointer', color: 'var(--text-secondary)' }}>
          Use my location as routing origin
        </label>
      </div>
    </div>
  );
}
