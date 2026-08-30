import React from 'react';

export function DepotPanel({ depots = [] }) {
  return (
    <div className="panel-card">
      <div className="panel-header">
        <span className="panel-title">
          <span>🏭</span> Depot Hubs ({depots.length})
        </span>
        <span style={{ fontSize: '10px', color: 'var(--text-muted)' }}>View All</span>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
        {depots.map(d => {
          const isW1 = d.id === 'W1';
          const badgeBg = isW1 ? '#8b5cf6' : '#10b981';

          return (
            <div key={d.id} className="destination-item">
              <div className="dest-info">
                <div className="dest-badge" style={{ backgroundColor: badgeBg, borderRadius: '4px' }}>
                  {d.id}
                </div>
                <div>
                  <div style={{ fontWeight: '600', color: '#f8fafc' }}>
                    {d.name || (isW1 ? 'Peenya Industrial Area, Bengaluru' : 'Hosur Road Logistics Hub, Bengaluru')}
                  </div>
                  <div style={{ color: 'var(--text-muted)', fontSize: '10px', fontFamily: 'var(--font-mono)' }}>
                    {d.latitude ? `${d.latitude.toFixed(4)}° N, ${d.longitude.toFixed(4)}° E` : '--'}
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
