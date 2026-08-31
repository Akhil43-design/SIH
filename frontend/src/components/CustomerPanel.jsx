import React, { useState } from 'react';
import { PRIORITY_COLORS } from '../utils/constants';
import { api } from '../services/api';
import { CsvImportModal } from './CsvImportModal';
import { LocationPicker } from './LocationPicker';

export function CustomerPanel({ customers = [], activeCity, onDataChanged }) {
  const [showAll, setShowAll] = useState(false);
  const [isCsvModalOpen, setIsCsvModalOpen] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    id: '', name: '', city: '', latitude: '', longitude: '', demand: '',
    priority: 'MEDIUM', timeWindowStart: '', timeWindowEnd: '', serviceTimeMins: ''
  });

  const displayList = showAll ? customers : customers.slice(0, 3);
  const remainingCount = Math.max(0, customers.length - 3);

  const resetForm = () => {
    setFormData({
      id: '', name: '', city: '', latitude: '', longitude: '', demand: '',
      priority: 'MEDIUM', timeWindowStart: '', timeWindowEnd: '', serviceTimeMins: ''
    });
    setEditingId(null);
    setShowForm(false);
  };

  const handleEdit = (c) => {
    setFormData({
      id: c.id, name: c.name || '', city: c.city || '', 
      latitude: c.latitude, longitude: c.longitude, 
      demand: c.demandKg ?? c.demand ?? 0, priority: c.priority || 'MEDIUM',
      timeWindowStart: c.timeWindowStart || '', timeWindowEnd: c.timeWindowEnd || '',
      serviceTimeMins: c.serviceTimeMins || 0
    });
    setEditingId(c.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this customer?')) {
      try {
        await api.deleteCustomer(id);
        if (onDataChanged) onDataChanged();
      } catch (err) {
        alert('Failed to delete customer: ' + err.message);
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
        longitude: parseFloat(formData.longitude),
        demand: parseFloat(formData.demand),
        priority: formData.priority,
        timeWindowStart: formData.timeWindowStart,
        timeWindowEnd: formData.timeWindowEnd,
        serviceTimeMins: parseInt(formData.serviceTimeMins) || 0
      };

      if (editingId) {
        await api.updateCustomer(editingId, payload);
      } else {
        await api.createCustomer(payload);
      }
      
      resetForm();
      if (onDataChanged) onDataChanged();
      alert(`Customer ${editingId ? 'updated' : 'created'} successfully.`);
    } catch (err) {
      alert(`Error saving customer: ` + err.message);
    }
  };

  return (
    <div className="panel-card">
      <div className="panel-header" style={{flexWrap: 'wrap', gap: '5px'}}>
        <span className="panel-title">
          <span>📦</span> Customer Management ({customers.length})
        </span>
        <div style={{display: 'flex', gap: '5px'}}>
          <button className="btn btn-primary btn-sm" onClick={() => setShowForm(!showForm)}>
            {showForm ? 'Cancel' : '+ Add Customer'}
          </button>
          <button className="btn btn-secondary btn-sm" onClick={() => setIsCsvModalOpen(true)}>
            📥 Import CSV
          </button>
        </div>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} style={{padding: '10px', backgroundColor: '#1e293b', borderRadius: '4px', marginBottom: '10px', fontSize: '12px'}}>
          <div style={{display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px', marginBottom: '10px'}}>
            <input required placeholder="Customer ID (e.g. BLR-C101)" value={formData.id} onChange={e => setFormData({...formData, id: e.target.value})} disabled={!!editingId} />
            <input required placeholder="Name (e.g. ABC Electronics)" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} />
            <input required placeholder="City (e.g. Bengaluru)" value={formData.city} onChange={e => setFormData({...formData, city: e.target.value})} />
            
            <div style={{ gridColumn: '1 / span 2' }}>
              <LocationPicker 
                activeCity={activeCity}
                initialLat={formData.latitude}
                initialLon={formData.longitude}
                onChange={(lat, lon) => setFormData({...formData, latitude: lat, longitude: lon})}
              />
            </div>
            <input required type="number" step="any" placeholder="Demand (kg) >= 0" min="0" value={formData.demand} onChange={e => setFormData({...formData, demand: e.target.value})} />
            <select value={formData.priority} onChange={e => setFormData({...formData, priority: e.target.value})}>
              <option value="HIGH">HIGH</option>
              <option value="MEDIUM">MEDIUM</option>
              <option value="LOW">LOW</option>
            </select>
            <input placeholder="Earliest Time (e.g. 10:00)" value={formData.timeWindowStart} onChange={e => setFormData({...formData, timeWindowStart: e.target.value})} />
            <input placeholder="Latest Time (e.g. 12:00)" value={formData.timeWindowEnd} onChange={e => setFormData({...formData, timeWindowEnd: e.target.value})} />
            <input type="number" placeholder="Service Time (mins)" min="0" value={formData.serviceTimeMins} onChange={e => setFormData({...formData, serviceTimeMins: e.target.value})} />
          </div>
          <div style={{display: 'flex', gap: '5px', justifyContent: 'flex-end'}}>
            <button type="button" className="btn btn-secondary btn-sm" onClick={resetForm}>Cancel</button>
            <button type="submit" className="btn btn-primary btn-sm">Save Customer</button>
          </div>
        </form>
      )}

      <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
        {displayList.map((c, idx) => {
          const color = PRIORITY_COLORS[c.priority] || '#3b82f6';
          const demand = c.demandKg != null ? c.demandKg : c.demand;
          const displayNum = (idx + 1);

          return (
            <div key={c.id} className="destination-item" style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
              <div className="dest-info">
                <div className="dest-badge" style={{ backgroundColor: color }}>
                  {displayNum}
                </div>
                <div>
                  <div style={{ fontWeight: '600', color: '#f8fafc' }}>
                    {c.name || c.id}
                  </div>
                  <div style={{ color: 'var(--text-muted)', fontSize: '10px' }}>
                    {c.priority} Priority • {demand.toFixed(1)} kg • {c.timeWindowStart && c.timeWindowEnd ? `${c.timeWindowStart}-${c.timeWindowEnd}` : 'No Time Window'}
                  </div>
                </div>
              </div>
              <div style={{display: 'flex', gap: '5px'}}>
                <button className="btn btn-secondary btn-sm" style={{fontSize: '10px', padding: '2px 5px'}} onClick={() => handleEdit(c)}>Edit</button>
                <button className="btn btn-secondary btn-sm" style={{fontSize: '10px', padding: '2px 5px', backgroundColor: '#ef4444', color: 'white'}} onClick={() => handleDelete(c.id)}>Del</button>
              </div>
            </div>
          );
        })}

        {!showAll && remainingCount > 0 && (
          <div
            onClick={() => setShowAll(true)}
            style={{
              fontSize: '11px', color: 'var(--text-muted)', fontStyle: 'italic', cursor: 'pointer', textAlign: 'center', padding: '4px 0'
            }}
          >
            ... and {remainingCount} more destinations
          </div>
        )}
        {showAll && customers.length > 3 && (
            <div
            onClick={() => setShowAll(false)}
            style={{
              fontSize: '11px', color: 'var(--text-muted)', fontStyle: 'italic', cursor: 'pointer', textAlign: 'center', padding: '4px 0'
            }}
          >
            Collapse list
          </div>
        )}
      </div>

      <CsvImportModal 
        isOpen={isCsvModalOpen} 
        onClose={() => setIsCsvModalOpen(false)} 
        onImportComplete={() => { if (onDataChanged) onDataChanged(); }} 
      />
    </div>
  );
}
