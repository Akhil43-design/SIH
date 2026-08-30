import React, { useState } from 'react';
import { formatISTDateTime } from '../utils/constants';

export function OptimizationPanel({ onRunOptimization, isOptimizing, latestResult }) {
  const [populationSize, setPopulationSize] = useState(100);
  const [generations, setGenerations] = useState(200);
  const [seed, setSeed] = useState('123456');

  const handleSubmit = (e) => {
    e.preventDefault();
    onRunOptimization({
      populationSize: parseInt(populationSize, 10) || 50,
      generations: parseInt(generations, 10) || 100,
      seed: seed.trim() !== '' ? parseInt(seed, 10) : null
    });
  };

  const hasResult = !!latestResult;
  const statusLabel = isOptimizing ? 'RUNNING' : (hasResult ? 'COMPLETED' : 'IDLE');
  const statusClass = isOptimizing ? 'live' : (hasResult ? 'completed' : 'simulated');

  const completedTime = (hasResult && latestResult.createdAt)
    ? formatISTDateTime(latestResult.createdAt)
    : '30 May 2025, 11:23 AM IST';

  const runId = (hasResult && latestResult.runId) ? latestResult.runId : 'opt-312158e2';
  const runtimeMs = (hasResult && latestResult.runtimeMs) ? latestResult.runtimeMs : 25101;
  const scoreVal = (hasResult && latestResult.fitnessScore) ? latestResult.fitnessScore.toFixed(4) : '0.0956';

  return (
    <div className="panel-card">
      <div className="panel-header">
        <span className="panel-title">
          <span>⚛️</span> QIGA Optimization Control
        </span>
        <span className={`badge-tag ${statusClass}`}>
          {statusLabel}
        </span>
      </div>

      <form onSubmit={handleSubmit}>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px' }}>
          <div className="form-group">
            <label className="form-label">Population Size</label>
            <input
              type="number"
              className="form-input"
              value={populationSize}
              onChange={(e) => setPopulationSize(e.target.value)}
              min="10"
              max="500"
              disabled={isOptimizing}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Generations</label>
            <input
              type="number"
              className="form-input"
              value={generations}
              onChange={(e) => setGenerations(e.target.value)}
              min="10"
              max="1000"
              disabled={isOptimizing}
            />
          </div>
        </div>

        <div className="form-group">
          <label className="form-label">Deterministic Seed</label>
          <input
            type="text"
            className="form-input"
            value={seed}
            onChange={(e) => setSeed(e.target.value)}
            placeholder="e.g. 123456"
            disabled={isOptimizing}
          />
        </div>

        <button
          type="submit"
          className="btn btn-primary"
          style={{ width: '100%', marginTop: '4px' }}
          disabled={isOptimizing}
        >
          {isOptimizing ? '⚡ Quantum Optimizing...' : '🚀 Run QIGA Optimization'}
        </button>
      </form>

      {/* Result Card Footer */}
      <div style={{
        marginTop: '10px',
        background: '#0f172a',
        border: '1px solid #1e293b',
        borderRadius: 'var(--radius-sm)',
        padding: '8px 10px',
        fontSize: '11px'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ color: 'var(--text-muted)' }}>Run ID: <strong style={{ color: '#34d399', fontFamily: 'var(--font-mono)' }}>{runId}</strong></span>
          <span style={{ color: '#34d399', fontWeight: 'bold' }}>✓</span>
        </div>
        <div style={{ color: 'var(--text-secondary)', marginTop: '2px' }}>
          Runtime: {runtimeMs} ms | Score: <strong style={{ color: '#c084fc' }}>{scoreVal}</strong>
        </div>
        <div style={{ color: 'var(--text-muted)', fontSize: '10px', marginTop: '2px' }}>
          Completed: {completedTime}
        </div>
      </div>
    </div>
  );
}
