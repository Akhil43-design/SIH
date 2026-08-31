import React, { useState } from 'react';
import { api } from '../services/api';

export function CsvImportModal({ isOpen, onClose, onImportComplete }) {
  const [file, setFile] = useState(null);
  const [parsedRows, setParsedRows] = useState([]);
  const [validRows, setValidRows] = useState([]);
  const [invalidRows, setInvalidRows] = useState([]);
  const [isImporting, setIsImporting] = useState(false);

  if (!isOpen) return null;

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      setFile(selectedFile);
      parseCSV(selectedFile);
    }
  };

  const parseCSV = (file) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      const text = e.target.result;
      const lines = text.split('\n').map(l => l.trim()).filter(l => l.length > 0);
      
      if (lines.length < 2) {
        setInvalidRows([{ rowNum: 0, error: 'CSV must contain headers and at least one data row' }]);
        return;
      }
      
      const headers = lines[0].split(',').map(h => h.trim().toLowerCase());
      const valid = [];
      const invalid = [];
      
      for (let i = 1; i < lines.length; i++) {
        const cols = lines[i].split(',').map(c => c.trim());
        // Expected: customer_id,name,city,latitude,longitude,demand,priority,earliest_time,latest_time,service_time
        
        let rowObj = {};
        for(let j=0; j<headers.length; j++) {
            rowObj[headers[j]] = cols[j];
        }
        
        let error = null;
        if (!rowObj.customer_id) error = 'Missing customer_id';
        else if (isNaN(parseFloat(rowObj.latitude)) || parseFloat(rowObj.latitude) < -90 || parseFloat(rowObj.latitude) > 90) error = 'Invalid latitude';
        else if (isNaN(parseFloat(rowObj.longitude)) || parseFloat(rowObj.longitude) < -180 || parseFloat(rowObj.longitude) > 180) error = 'Invalid longitude';
        else if (isNaN(parseFloat(rowObj.demand)) || parseFloat(rowObj.demand) < 0) error = 'Negative or invalid demand';
        else if (!['HIGH', 'MEDIUM', 'LOW'].includes(rowObj.priority)) error = 'Invalid priority';
        
        if (error) {
          invalid.push({ rowNum: i + 1, error: error, raw: lines[i] });
        } else {
          valid.push({
            id: rowObj.customer_id,
            name: rowObj.name || rowObj.customer_id,
            city: rowObj.city,
            latitude: parseFloat(rowObj.latitude),
            longitude: parseFloat(rowObj.longitude),
            demand: parseFloat(rowObj.demand),
            priority: rowObj.priority,
            timeWindowStart: rowObj.earliest_time,
            timeWindowEnd: rowObj.latest_time,
            serviceTimeMins: parseFloat(rowObj.service_time) || 0
          });
        }
      }
      
      setParsedRows(lines.slice(1));
      setValidRows(valid);
      setInvalidRows(invalid);
    };
    reader.readAsText(file);
  };

  const handleImport = async () => {
    setIsImporting(true);
    try {
      for (const row of validRows) {
        await api.createCustomer(row);
      }
      onImportComplete();
      onClose();
    } catch (err) {
      alert("Error during import: " + err.message);
    } finally {
      setIsImporting(false);
    }
  };

  return (
    <div className="modal-overlay" style={{
      position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
      backgroundColor: 'rgba(0,0,0,0.7)', zIndex: 1000,
      display: 'flex', alignItems: 'center', justifyContent: 'center'
    }}>
      <div className="modal-content" style={{
        backgroundColor: '#1e293b', padding: '20px', borderRadius: '8px',
        width: '600px', maxHeight: '80vh', overflowY: 'auto',
        color: '#f8fafc', boxShadow: '0 4px 6px rgba(0,0,0,0.3)'
      }}>
        <h2 style={{marginTop: 0}}>📥 Import Customers (CSV)</h2>
        <p style={{fontSize: '12px', color: '#94a3b8'}}>
          Format: customer_id, name, city, latitude, longitude, demand, priority, earliest_time, latest_time, service_time
        </p>
        
        <input type="file" accept=".csv" onChange={handleFileChange} style={{marginBottom: '15px'}} />
        
        {parsedRows.length > 0 && (
          <div style={{marginTop: '15px', backgroundColor: '#0f172a', padding: '10px', borderRadius: '4px'}}>
            <h4>Import Summary</h4>
            <div style={{display: 'flex', gap: '20px', marginBottom: '10px'}}>
              <div style={{color: '#4ade80'}}>Valid rows: {validRows.length}</div>
              <div style={{color: '#f87171'}}>Invalid rows: {invalidRows.length}</div>
            </div>
            
            {invalidRows.length > 0 && (
              <div style={{fontSize: '12px', color: '#f87171', maxHeight: '150px', overflowY: 'auto'}}>
                <strong>Errors:</strong>
                <ul style={{paddingLeft: '20px', margin: '5px 0'}}>
                  {invalidRows.slice(0, 20).map((err, i) => (
                    <li key={i}>Row {err.rowNum}: {err.error}</li>
                  ))}
                  {invalidRows.length > 20 && <li>... and {invalidRows.length - 20} more errors</li>}
                </ul>
              </div>
            )}
          </div>
        )}
        
        <div style={{display: 'flex', gap: '10px', marginTop: '20px', justifyContent: 'flex-end'}}>
          <button className="btn btn-secondary" onClick={onClose} disabled={isImporting}>
            Cancel
          </button>
          <button 
            className="btn btn-primary" 
            onClick={handleImport} 
            disabled={validRows.length === 0 || isImporting}
          >
            {isImporting ? 'Importing...' : `Import ${validRows.length} Valid Rows`}
          </button>
        </div>
      </div>
    </div>
  );
}
