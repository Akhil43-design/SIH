import React, { useState } from 'react';

export function DepotPanel({ depots, vehicles, onAddDepot }) {
  const [showAddModal, setShowAddModal] = useState(false);
  const [newDepot, setNewDepot] = useState({
    id: `W${(depots || []).length + 1}`,
    name: 'East London Logistics Hub',
    latitude: 51.52,
    longitude: -0.05
  });

  // Count vehicles per depot
  const vehicleCountMap = new Map();
  (vehicles || []).forEach((v) => {
    if (v.depotId) {
      vehicleCountMap.set(v.depotId, (vehicleCountMap.get(v.depotId) || 0) + 1);
    }
  });

  const handleCreate = (e) => {
    e.preventDefault();
    onAddDepot({
      ...newDepot,
      latitude: Number(newDepot.latitude),
      longitude: Number(newDepot.longitude)
    });
    setShowAddModal(false);
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
        <span style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>
          {(depots || []).length} Multi-Depot Logistics Hubs
        </span>
        <button
          className="btn btn-secondary"
          style={{ padding: '4px 10px', fontSize: '11px' }}
          onClick={() => setShowAddModal(true)}
        >
          + Add Depot
        </button>
      </div>

      <div className="table-responsive">
        <table className="data-table">
          <thead>
            <tr>
              <th>Depot Hub</th>
              <th>Coordinates</th>
              <th>Stationed Vehicles</th>
            </tr>
          </thead>
          <tbody>
            {(depots || []).map((d) => {
              const vCount = vehicleCountMap.get(d.id) || 0;
              return (
                <tr key={d.id}>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <span style={{ fontSize: '16px' }}>🏭</span>
                      <div>
                        <strong>{d.name || d.id}</strong>
                        <div style={{ fontSize: '10px', color: 'var(--text-muted)' }}>
                          <code>{d.id}</code>
                        </div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <code>[{d.latitude?.toFixed(4)}, {d.longitude?.toFixed(4)}]</code>
                  </td>
                  <td>
                    <span className="badge badge-completed">
                      🚛 {vCount} {vCount === 1 ? 'Vehicle' : 'Vehicles'}
                    </span>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* Add Depot Modal */}
      {showAddModal && (
        <div className="modal-overlay" onClick={() => setShowAddModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3 style={{ fontSize: '15px', fontWeight: '700', marginBottom: '14px' }}>Add Depot Logistics Hub</h3>
            <form onSubmit={handleCreate}>
              <div className="form-group">
                <label>Depot ID</label>
                <input
                  type="text"
                  className="form-input"
                  value={newDepot.id}
                  onChange={(e) => setNewDepot({ ...newDepot, id: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Depot Name</label>
                <input
                  type="text"
                  className="form-input"
                  value={newDepot.name}
                  onChange={(e) => setNewDepot({ ...newDepot, name: e.target.value })}
                  required
                />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Latitude</label>
                  <input
                    type="number"
                    step="0.0001"
                    className="form-input"
                    value={newDepot.latitude}
                    onChange={(e) => setNewDepot({ ...newDepot, latitude: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Longitude</label>
                  <input
                    type="number"
                    step="0.0001"
                    className="form-input"
                    value={newDepot.longitude}
                    onChange={(e) => setNewDepot({ ...newDepot, longitude: e.target.value })}
                    required
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
                  Save Depot
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
