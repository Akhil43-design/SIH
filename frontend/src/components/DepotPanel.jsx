import React, { useState } from 'react';
import { api } from '../services/api';

export function DepotPanel({ depots = [], onDataChanged }) {
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    id: '', name: '', city: '', latitude: '', longitude: ''
  });

  const resetForm = () => {
    setFormData({ id: '', name: '', city: '', latitude: '', longitude: '' });
    setEditingId(null);
    setShowForm(false);
  };

  const handleEdit = (d) => {
    setFormData({
      id: d.id, name: d.name || '', city: d.city || '', 
      latitude: d.latitude, longitude: d.longitude
    });
    setEditingId(d.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this depot?')) {
      try {
        await api.deleteDepot(id);
        if (onDataChanged) onDataChanged();
      } catch (err) {
        alert('Failed to delete depot: ' + err.message);
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        id: formData.id,
        name: formData.name || formData.id,
        city: formData.city,
        latitude: parseFloat(formData.latitude),
        longitude: parseFloat(formData.longitude)
      };

      if (editingId) {
        await api.updateDepot(editingId, payload);
      } else {
        await api.createDepot(payload);
      }
      
      resetForm();
      if (onDataChanged) onDataChanged();
      alert(`Depot ${editingId ? 'updated' : 'created'} successfully.`);
    } catch (err) {
      alert(`Error saving depot: ` + err.message);
    }
  };

  return (
    <div className="panel-card">
      <div className="panel-header" style={{flexWrap: 'wrap', gap: '5px'}}>
        <span className="panel-title">
          <span>🏭</span> Depot Hubs ({depots.length})
        </span>
        <button className="btn btn-primary btn-sm" onClick={() => setShowForm(!showForm)}>
          {showForm ? 'Cancel' : '+ Add Depot'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} style={{padding: '10px', backgroundColor: '#1e293b', borderRadius: '4px', marginBottom: '10px', fontSize: '12px'}}>
          <div style={{display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px', marginBottom: '10px'}}>
            <input required placeholder="Depot ID (e.g. BLR-W3)" value={formData.id} onChange={e => setFormData({...formData, id: e.target.value})} disabled={!!editingId} />
            <input required placeholder="Depot Name" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} />
            <input required placeholder="City (e.g. Bengaluru)" value={formData.city} onChange={e => setFormData({...formData, city: e.target.value})} />
            <input required type="number" step="any" placeholder="Latitude (-90 to 90)" min="-90" max="90" value={formData.latitude} onChange={e => setFormData({...formData, latitude: e.target.value})} />
            <input required type="number" step="any" placeholder="Longitude (-180 to 180)" min="-180" max="180" value={formData.longitude} onChange={e => setFormData({...formData, longitude: e.target.value})} />
          </div>
          <div style={{display: 'flex', gap: '5px', justifyContent: 'flex-end'}}>
            <button type="button" className="btn btn-secondary btn-sm" onClick={resetForm}>Cancel</button>
            <button type="submit" className="btn btn-primary btn-sm">Save Depot</button>
          </div>
        </form>
      )}

      <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
        {depots.map(d => {
          const isW1 = d.id === 'W1' || d.id === 'BLR-W1';
          const badgeBg = isW1 ? '#8b5cf6' : '#10b981';

          return (
            <div key={d.id} className="destination-item" style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
              <div className="dest-info">
                <div className="dest-badge" style={{ backgroundColor: badgeBg, borderRadius: '4px' }}>
                  {d.id}
                </div>
                <div>
                  <div style={{ fontWeight: '600', color: '#f8fafc' }}>
                    {d.name || d.id}
                  </div>
                  <div style={{ color: 'var(--text-muted)', fontSize: '10px', fontFamily: 'var(--font-mono)' }}>
                    {d.latitude ? `${d.latitude.toFixed(4)}° N, ${d.longitude.toFixed(4)}° E` : '--'}
                  </div>
                </div>
              </div>
              <div style={{display: 'flex', gap: '5px'}}>
                <button className="btn btn-secondary btn-sm" style={{fontSize: '10px', padding: '2px 5px'}} onClick={() => handleEdit(d)}>Edit</button>
                <button className="btn btn-secondary btn-sm" style={{fontSize: '10px', padding: '2px 5px', backgroundColor: '#ef4444', color: 'white'}} onClick={() => handleDelete(d.id)}>Del</button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
