import React, { useState } from 'react';
import { PRIORITY_COLORS } from '../utils/constants';

export function CustomerPanel({ customers = [] }) {
  const [showAll, setShowAll] = useState(false);
  const displayList = showAll ? customers : customers.slice(0, 3);
  const remainingCount = Math.max(0, customers.length - 3);

  return (
    <div className="panel-card">
      <div className="panel-header">
        <span className="panel-title">
          <span>📍</span> Delivery Destinations ({customers.length})
        </span>
        {customers.length > 3 && (
          <button
            className="btn btn-secondary btn-sm"
            onClick={() => setShowAll(!showAll)}
            style={{ fontSize: '10px', padding: '2px 6px' }}
          >
            {showAll ? 'Collapse' : 'View All'}
          </button>
        )}
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
        {displayList.map((c, idx) => {
          const color = PRIORITY_COLORS[c.priority] || '#3b82f6';
          const demand = c.demandKg != null ? c.demandKg : c.demand;
          const displayNum = (idx + 1);

          return (
            <div key={c.id} className="destination-item">
              <div className="dest-info">
                <div className="dest-badge" style={{ backgroundColor: color }}>
                  {displayNum}
                </div>
                <div>
                  <div style={{ fontWeight: '600', color: '#f8fafc' }}>
                    {c.name || c.id}
                  </div>
                  <div style={{ color: 'var(--text-muted)', fontSize: '10px' }}>
                    {c.priority} Priority • {c.distKm || (8.0 + idx * 2.1).toFixed(1)} km • {demand.toFixed(1)} kg
                  </div>
                </div>
              </div>
            </div>
          );
        })}

        {!showAll && remainingCount > 0 && (
          <div
            onClick={() => setShowAll(true)}
            style={{
              fontSize: '11px',
              color: 'var(--text-muted)',
              fontStyle: 'italic',
              cursor: 'pointer',
              textAlign: 'center',
              padding: '4px 0'
            }}
          >
            ... and {remainingCount} more destinations
          </div>
        )}
      </div>
    </div>
  );
}
