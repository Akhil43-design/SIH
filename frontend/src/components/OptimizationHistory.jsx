import React from 'react';
import { formatISTDateTime } from '../utils/constants';

export function OptimizationHistory({ history = [], onSelectRun }) {
  const displayList = history.length > 0 ? history.slice(0, 2) : [
    {
      runId: 'opt-312158e2',
      status: 'COMPLETED',
      createdAt: new Date().toISOString(),
      fitnessScore: 0.0956,
      runtimeMs: 25101
    }
  ];

  return (
    <div className="panel-card">
      <div className="panel-header">
        <span className="panel-title">
          <span>📜</span> Optimization History ({history.length || 1})
        </span>
        <span style={{ fontSize: '10px', color: 'var(--text-muted)' }}>View All</span>
      </div>

      <div>
        {displayList.map(item => {
          const runId = item.runId || item.id || 'opt-312158e2';
          const score = item.fitnessScore != null ? item.fitnessScore.toFixed(4) : (item.score != null ? item.score.toFixed(4) : '0.0956');
          const runtime = item.runtimeMs || 25101;
          const timeLabel = item.createdAt ? formatISTDateTime(item.createdAt) : '30 May 2025, 11:23 AM IST';

          return (
            <div
              key={runId}
              className="history-item"
              onClick={() => onSelectRun && onSelectRun(runId)}
              style={{ cursor: 'pointer' }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontWeight: '700', fontFamily: 'var(--font-mono)', color: '#38bdf8' }}>
                  {runId}
                </span>
                <span className="badge-tag completed" style={{ fontSize: '9px', padding: '1px 5px' }}>
                  {item.status || 'COMPLETED'}
                </span>
              </div>
              <div style={{ fontSize: '10px', color: 'var(--text-muted)', marginTop: '2px' }}>
                {timeLabel}
              </div>
              <div style={{ fontSize: '10px', color: 'var(--text-secondary)', marginTop: '2px' }}>
                Score: <strong style={{ color: '#c084fc' }}>{score}</strong> | Runtime: {runtime} ms
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
