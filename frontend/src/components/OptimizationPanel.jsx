import React, { useState } from 'react';

export function OptimizationPanel({ onRunOptimization, isOptimizing, lastOptimization }) {
  const [popSize, setPopSize] = useState(30);
  const [generations, setGenerations] = useState(50);
  const [seed, setSeed] = useState(42);

  const handleRun = () => {
    if (isOptimizing) return;
    onRunOptimization({
      populationSize: Number(popSize),
      generations: Number(generations),
      seed: Number(seed)
    });
  };

  return (
    <div className="panel-card">
      <div className="card-header" style={{ margin: '-16px -16px 14px -16px', borderRadius: '12px 12px 0 0' }}>
        <h2>⚛️ QIGA Optimization Control</h2>
        {lastOptimization && (
          <span className="badge badge-completed">
            {lastOptimization.status}
          </span>
        )}
      </div>

      <div className="form-row">
        <div className="form-group">
          <label>Population Size</label>
          <input
            type="number"
            className="form-input"
            value={popSize}
            min={10}
            max={200}
            onChange={(e) => setPopSize(e.target.value)}
            disabled={isOptimizing}
          />
        </div>

        <div className="form-group">
          <label>Generations</label>
          <input
            type="number"
            className="form-input"
            value={generations}
            min={10}
            max={500}
            onChange={(e) => setGenerations(e.target.value)}
            disabled={isOptimizing}
          />
        </div>
      </div>

      <div className="form-group">
        <label>Deterministic Seed</label>
        <input
          type="number"
          className="form-input"
          value={seed}
          onChange={(e) => setSeed(e.target.value)}
          disabled={isOptimizing}
        />
      </div>

      <button
        className="btn btn-primary"
        style={{ width: '100%', marginTop: '6px', padding: '12px' }}
        onClick={handleRun}
        disabled={isOptimizing}
      >
        {isOptimizing ? '⚡ Running Quantum-Inspired Optimization...' : '🚀 Run QIGA Optimization'}
      </button>

      {lastOptimization && (
        <div style={{ marginTop: '12px', fontSize: '11px', color: 'var(--text-secondary)' }}>
          <div>Run ID: <code>{lastOptimization.optimizationId}</code></div>
          <div>Runtime: <strong>{lastOptimization.runtimeMs || '<1'} ms</strong> | Score: <strong>{lastOptimization.optimizationScore?.toFixed(4)}</strong></div>
        </div>
      )}
    </div>
  );
}
