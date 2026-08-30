import React from 'react';
import { formatTime } from '../utils/constants';

export function OptimizationHistory({ history, currentOptId, onSelectRun }) {
  return (
    <div className="table-responsive">
      <table className="data-table">
        <thead>
          <tr>
            <th>Run ID</th>
            <th>Status</th>
            <th>Distance</th>
            <th>Time</th>
            <th>Score</th>
            <th>Runtime</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {(history || []).map((run) => {
            const isCurrent = currentOptId === run.id;
            return (
              <tr
                key={run.id}
                style={{
                  backgroundColor: isCurrent ? 'rgba(99, 102, 241, 0.15)' : undefined
                }}
              >
                <td>
                  <code>{run.id}</code>
                  {run.parentRunId && (
                    <div style={{ fontSize: '10px', color: 'var(--accent-cyan)' }}>
                      ↳ Rev of {run.parentRunId}
                    </div>
                  )}
                </td>
                <td>
                  <span className={`badge badge-${(run.status || 'completed').toLowerCase()}`}>
                    {run.status}
                  </span>
                </td>
                <td>
                  {run.requestedCustomerCount ? `${run.requestedCustomerCount} Stops` : '--'}
                </td>
                <td>
                  {run.routingMode || 'OSRM'}
                </td>
                <td>
                  <strong>{run.trafficMode || 'SIMULATED'}</strong>
                </td>
                <td>
                  {run.runtimeMs ? `${run.runtimeMs} ms` : '<1 ms'}
                </td>
                <td>
                  <button
                    className="btn btn-secondary"
                    style={{ padding: '2px 8px', fontSize: '10px' }}
                    onClick={() => onSelectRun(run.id)}
                  >
                    View Plan
                  </button>
                </td>
              </tr>
            );
          })}
          {(!history || history.length === 0) && (
            <tr>
              <td colSpan={7} style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '16px' }}>
                No optimization history records found.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
