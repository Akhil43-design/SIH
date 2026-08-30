import React, { useState } from 'react';
import { BeforeAfterComparison } from './BeforeAfterComparison';

export function DynamicReoptModal({
  isOpen,
  onClose,
  depots,
  customers,
  lastOptimization,
  onTriggerReoptimization,
  reoptResult,
  isProcessing
}) {
  const [originId, setOriginId] = useState(depots && depots[0] ? depots[0].id : 'W1');
  const [destId, setDestId] = useState(customers && customers[0] ? customers[0].id : 'C1');
  const [multiplier, setMultiplier] = useState(2.5);

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    if (isProcessing) return;

    onTriggerReoptimization({
      originId,
      destinationId: destId,
      oldMultiplier: 1.0,
      newMultiplier: Number(multiplier),
      timestamp: Date.now(),
      source: 'SIMULATED_SURGE_EVENT'
    });
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '640px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <h3 style={{ fontSize: '16px', fontWeight: '700' }}>🚨 Dynamic Traffic Event & Re-Optimization Demo</h3>
          <button
            onClick={onClose}
            style={{ background: 'transparent', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer', fontSize: '18px' }}
          >
            ✕
          </button>
        </div>

        <p style={{ fontSize: '12px', color: 'var(--text-secondary)', marginBottom: '16px' }}>
          Problem Statement 137 Core Capability: Inject sudden arterial congestion on an active road segment, triggering immediate QIGA fleet re-optimization while protecting already-serviced stops.
        </p>

        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>Origin Hub / Stop</label>
              <select
                className="form-input"
                value={originId}
                onChange={(e) => setOriginId(e.target.value)}
                disabled={isProcessing}
              >
                {(depots || []).map((d) => (
                  <option key={d.id} value={d.id}>
                    🏭 {d.name || d.id} ({d.id})
                  </option>
                ))}
                {(customers || []).map((c) => (
                  <option key={c.id} value={c.id}>
                    📍 {c.name || c.id} ({c.id})
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Destination Stop</label>
              <select
                className="form-input"
                value={destId}
                onChange={(e) => setDestId(e.target.value)}
                disabled={isProcessing}
              >
                {(customers || []).map((c) => (
                  <option key={c.id} value={c.id}>
                    📍 {c.name || c.id} ({c.id})
                  </option>
                ))}
                {(depots || []).map((d) => (
                  <option key={d.id} value={d.id}>
                    🏭 {d.name || d.id} ({d.id})
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-group">
            <label>Congestion Multiplier (1.0x = Normal, 3.0x = Severe Gridlock)</label>
            <input
              type="number"
              step="0.1"
              min="1.0"
              max="5.0"
              className="form-input"
              value={multiplier}
              onChange={(e) => setMultiplier(e.target.value)}
              disabled={isProcessing}
            />
          </div>

          <button
            type="submit"
            className="btn btn-cyan"
            style={{ width: '100%', padding: '10px' }}
            disabled={isProcessing || !lastOptimization}
          >
            {isProcessing ? '⚡ Recalculating Optimal Fleet Routes...' : '⚡ Inject Congestion & Re-Optimize Fleet'}
          </button>
        </form>

        {/* Before vs After Comparison Result */}
        {reoptResult && lastOptimization && (
          <BeforeAfterComparison
            before={lastOptimization}
            after={reoptResult}
          />
        )}
      </div>
    </div>
  );
}
