import React, { useState } from 'react';
import { formatClockTime } from '../utils/constants';

export function CustomerPanel({ customers, optimization, onAddCustomer, onCancelCustomer }) {
  const [showAddModal, setShowAddModal] = useState(false);
  const [newCust, setNewCust] = useState({
    id: `C${(customers || []).length + 1}`,
    name: 'New Delivery Stop',
    latitude: 51.515,
    longitude: -0.13,
    demand: 20,
    priority: 'HIGH',
    serviceTime: 5,
    earliestTime: 60,
    latestTime: 360
  });

  // Lookup assigned vehicle from optimization result
  const assignedVehicleMap = new Map();
  if (optimization && optimization.vehicleRoutes) {
    optimization.vehicleRoutes.forEach((vr) => {
      (vr.customerSequence || []).forEach((cid) => {
        assignedVehicleMap.set(cid, vr.vehicleId);
      });
    });
  }

  const handleCreate = (e) => {
    e.preventDefault();
    onAddCustomer({
      ...newCust,
      demand: Number(newCust.demand),
      latitude: Number(newCust.latitude),
      longitude: Number(newCust.longitude),
      serviceTime: Number(newCust.serviceTime),
      earliestTime: Number(newCust.earliestTime),
      latestTime: Number(newCust.latestTime)
    });
    setShowAddModal(false);
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
        <span style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>
          {(customers || []).length} Delivery Destinations Configured
        </span>
        <button
          className="btn btn-secondary"
          style={{ padding: '4px 10px', fontSize: '11px' }}
          onClick={() => setShowAddModal(true)}
        >
          + Add Customer
        </button>
      </div>

      <div className="table-responsive">
        <table className="data-table">
          <thead>
            <tr>
              <th>Customer</th>
              <th>Demand</th>
              <th>Priority</th>
              <th>Time Window</th>
              <th>Assigned Vehicle</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {(customers || []).map((c) => {
              const assignedVeh = assignedVehicleMap.get(c.id);
              const priorityClass = `badge-${(c.priority || 'medium').toLowerCase()}`;
              const isCancelled = !!c.cancelled;

              return (
                <tr key={c.id} style={{ opacity: isCancelled ? 0.5 : 1 }}>
                  <td>
                    <strong>{c.name || c.id}</strong>
                    <div style={{ fontSize: '10px', color: 'var(--text-muted)' }}>
                      <code>{c.id}</code> • [{c.latitude?.toFixed(3)}, {c.longitude?.toFixed(3)}]
                    </div>
                  </td>
                  <td>
                    <strong>{c.demand} kg</strong>
                  </td>
                  <td>
                    <span className={`badge ${priorityClass}`}>{c.priority || 'MEDIUM'}</span>
                  </td>
                  <td>
                    <span style={{ fontSize: '11px', fontFamily: 'var(--font-mono)' }}>
                      {formatClockTime(c.earliestTime)} – {formatClockTime(c.latestTime)}
                    </span>
                  </td>
                  <td>
                    {assignedVeh ? (
                      <span className="badge badge-completed">🚛 {assignedVeh}</span>
                    ) : (
                      <span className="badge badge-pending">{isCancelled ? 'Cancelled' : 'Unassigned'}</span>
                    )}
                  </td>
                  <td>
                    {!isCancelled && (
                      <button
                        className="btn btn-danger"
                        style={{ padding: '2px 6px', fontSize: '10px' }}
                        onClick={() => onCancelCustomer(c.id)}
                        title="Cancel customer demand dynamically"
                      >
                        Cancel
                      </button>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* Add Customer Modal */}
      {showAddModal && (
        <div className="modal-overlay" onClick={() => setShowAddModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3 style={{ fontSize: '15px', fontWeight: '700', marginBottom: '14px' }}>Add Customer Destination</h3>
            <form onSubmit={handleCreate}>
              <div className="form-row">
                <div className="form-group">
                  <label>Customer ID</label>
                  <input
                    type="text"
                    className="form-input"
                    value={newCust.id}
                    onChange={(e) => setNewCust({ ...newCust, id: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Customer Name</label>
                  <input
                    type="text"
                    className="form-input"
                    value={newCust.name}
                    onChange={(e) => setNewCust({ ...newCust, name: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Latitude</label>
                  <input
                    type="number"
                    step="0.0001"
                    className="form-input"
                    value={newCust.latitude}
                    onChange={(e) => setNewCust({ ...newCust, latitude: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Longitude</label>
                  <input
                    type="number"
                    step="0.0001"
                    className="form-input"
                    value={newCust.longitude}
                    onChange={(e) => setNewCust({ ...newCust, longitude: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Demand (kg)</label>
                  <input
                    type="number"
                    step="1"
                    className="form-input"
                    value={newCust.demand}
                    onChange={(e) => setNewCust({ ...newCust, demand: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Delivery Priority</label>
                  <select
                    className="form-input"
                    value={newCust.priority}
                    onChange={(e) => setNewCust({ ...newCust, priority: e.target.value })}
                  >
                    <option value="HIGH">HIGH (Urgent, 1.5x penalty)</option>
                    <option value="MEDIUM">MEDIUM (Standard, 1.0x)</option>
                    <option value="LOW">LOW (Flexible, 0.7x)</option>
                  </select>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Earliest Window (mins from 00:00)</label>
                  <input
                    type="number"
                    className="form-input"
                    value={newCust.earliestTime}
                    onChange={(e) => setNewCust({ ...newCust, earliestTime: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>Latest Window (mins from 00:00)</label>
                  <input
                    type="number"
                    className="form-input"
                    value={newCust.latestTime}
                    onChange={(e) => setNewCust({ ...newCust, latestTime: e.target.value })}
                  />
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '16px' }}>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setShowAddModal(false)}
                >
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Save Customer
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
