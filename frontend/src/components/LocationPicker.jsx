import React, { useState, useEffect, useRef, useCallback } from 'react';
import L from 'leaflet';

export function LocationPicker({ activeCity, initialLat, initialLon, onChange }) {
  const [mode, setMode] = useState('SEARCH'); // SEARCH or MANUAL
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const [searchError, setSearchError] = useState('');
  
  const [lat, setLat] = useState(initialLat || '');
  const [lon, setLon] = useState(initialLon || '');
  
  const mapContainerRef = useRef(null);
  const mapInstanceRef = useRef(null);
  const markerRef = useRef(null);
  const debounceTimerRef = useRef(null);

  // Initialize Map
  useEffect(() => {
    if (!mapContainerRef.current) return;

    let map = mapInstanceRef.current;
    if (!map) {
      try {
        const center = lat && lon ? [lat, lon] : [12.9716, 77.5946]; // default to Bengaluru
        map = L.map(mapContainerRef.current, {
          center: center,
          zoom: 13,
          attributionControl: false
        });

        L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
          maxZoom: 19,
          subdomains: 'abcd'
        }).addTo(map);

        const customIcon = L.divIcon({
          className: 'custom-leaflet-div-icon',
          html: `<div style="font-size: 24px; color: #ef4444; filter: drop-shadow(0 4px 4px rgba(0,0,0,0.5)); text-shadow: 0 0 2px white;">📍</div>`,
          iconSize: [24, 24],
          iconAnchor: [12, 24]
        });

        const marker = L.marker(center, {
          icon: customIcon,
          draggable: true
        }).addTo(map);

        marker.on('dragend', (e) => {
          const position = e.target.getLatLng();
          setLat(position.lat);
          setLon(position.lng);
          if (onChange) onChange(position.lat, position.lng);
        });

        markerRef.current = marker;
        mapInstanceRef.current = map;
      } catch (e) {
        console.warn('LocationPicker Map init:', e);
      }
    }

    return () => {
      if (mapInstanceRef.current) {
        mapInstanceRef.current.remove();
        mapInstanceRef.current = null;
      }
    };
  }, []);

  // Update map when lat/lon changes
  useEffect(() => {
    if (mapInstanceRef.current && markerRef.current && lat && lon && !isNaN(lat) && !isNaN(lon)) {
      const position = [parseFloat(lat), parseFloat(lon)];
      markerRef.current.setLatLng(position);
      mapInstanceRef.current.setView(position, 15);
    }
  }, [lat, lon]);

  const searchLocation = async (searchQuery) => {
    if (!searchQuery || searchQuery.length < 3) {
      setResults([]);
      return;
    }
    
    setIsSearching(true);
    setSearchError('');
    try {
      // Use Nominatim API for OpenStreetMap search. Limited to India.
      const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(searchQuery)}&format=json&addressdetails=1&countrycodes=in&limit=5`;
      const response = await fetch(url, {
        headers: {
          'Accept-Language': 'en-US,en;q=0.9',
          'User-Agent': 'QuantumRouteOptimizer-SIHDemo/1.0'
        }
      });
      
      if (!response.ok) {
        throw new Error('Geocoding service unavailable');
      }
      
      const data = await response.json();
      setResults(data);
    } catch (err) {
      console.error(err);
      setSearchError('Location search unavailable. Try manual entry.');
    } finally {
      setIsSearching(false);
    }
  };

  const handleQueryChange = (e) => {
    const val = e.target.value;
    setQuery(val);
    
    if (debounceTimerRef.current) clearTimeout(debounceTimerRef.current);
    
    debounceTimerRef.current = setTimeout(() => {
      searchLocation(val);
    }, 600);
  };

  const handleSelectResult = (result) => {
    // City Validation Logic
    const displayName = result.display_name || '';
    if (activeCity) {
      // Loose validation to check if the active city is part of the address
      // activeCity is usually like "bengaluru", "hyderabad", "mumbai"
      const cityRegex = new RegExp(activeCity, 'i');
      if (!cityRegex.test(displayName)) {
        const confirmMsg = `Location appears to be outside ${activeCity.charAt(0).toUpperCase() + activeCity.slice(1)}. Please select a location within the selected city. Continue anyway?`;
        if (!window.confirm(confirmMsg)) {
          return;
        }
      }
    }

    const resultLat = parseFloat(result.lat);
    const resultLon = parseFloat(result.lon);
    
    setLat(resultLat);
    setLon(resultLon);
    setQuery(displayName);
    setResults([]);
    
    if (onChange) onChange(resultLat, resultLon);
  };

  const handleManualChange = (field, val) => {
    if (field === 'lat') setLat(val);
    if (field === 'lon') setLon(val);
    if (onChange && !isNaN(parseFloat(val))) {
      if (field === 'lat') onChange(val, lon);
      if (field === 'lon') onChange(lat, val);
    }
  };

  return (
    <div style={{ backgroundColor: '#0f172a', padding: '10px', borderRadius: '6px', marginBottom: '10px', border: '1px solid #334155' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
        <strong style={{ color: '#f8fafc', fontSize: '13px' }}>Location</strong>
        <button 
          type="button" 
          onClick={() => setMode(mode === 'SEARCH' ? 'MANUAL' : 'SEARCH')}
          style={{ background: 'none', border: 'none', color: '#38bdf8', fontSize: '11px', cursor: 'pointer', textDecoration: 'underline' }}
        >
          {mode === 'SEARCH' ? 'Enter coordinates manually' : 'Use location search'}
        </button>
      </div>

      {mode === 'SEARCH' ? (
        <div style={{ position: 'relative', marginBottom: '10px' }}>
          <input
            type="text"
            placeholder={`Search Indian location (e.g. "Electronic City ${activeCity ? activeCity.charAt(0).toUpperCase() + activeCity.slice(1) : ''}")`}
            value={query}
            onChange={handleQueryChange}
            style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
          {isSearching && <div style={{ fontSize: '10px', color: '#fbbf24', marginTop: '4px' }}>Searching...</div>}
          {searchError && <div style={{ fontSize: '10px', color: '#ef4444', marginTop: '4px' }}>{searchError}</div>}
          
          {results.length > 0 && (
            <div style={{ position: 'absolute', top: '100%', left: 0, right: 0, backgroundColor: '#1e293b', border: '1px solid #334155', borderRadius: '4px', zIndex: 1000, maxHeight: '200px', overflowY: 'auto', boxShadow: '0 4px 6px rgba(0,0,0,0.3)' }}>
              {results.map((res, i) => (
                <div 
                  key={i} 
                  onClick={() => handleSelectResult(res)}
                  style={{ padding: '8px', borderBottom: '1px solid #334155', cursor: 'pointer', fontSize: '12px', color: '#e2e8f0' }}
                  onMouseEnter={(e) => e.target.style.backgroundColor = '#334155'}
                  onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
                >
                  {res.display_name}
                </div>
              ))}
            </div>
          )}
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px', marginBottom: '10px' }}>
          <input 
            required 
            type="number" 
            step="any" 
            placeholder="Latitude (-90 to 90)" 
            min="-90" max="90" 
            value={lat} 
            onChange={(e) => handleManualChange('lat', e.target.value)} 
          />
          <input 
            required 
            type="number" 
            step="any" 
            placeholder="Longitude (-180 to 180)" 
            min="-180" max="180" 
            value={lon} 
            onChange={(e) => handleManualChange('lon', e.target.value)} 
          />
        </div>
      )}

      {lat && lon && (
        <div style={{ marginBottom: '8px', fontSize: '11px', color: '#a3e635' }}>
          ✓ Coordinates: {Number(lat).toFixed(6)}, {Number(lon).toFixed(6)}
        </div>
      )}

      <div style={{ width: '100%', height: '150px', borderRadius: '4px', overflow: 'hidden', border: '1px solid #334155' }}>
        <div ref={mapContainerRef} style={{ width: '100%', height: '100%' }} />
      </div>
      <div style={{ fontSize: '10px', color: '#94a3b8', marginTop: '4px', textAlign: 'right' }}>
        Drag marker to fine-tune • Geocoding by OSM Nominatim
      </div>
    </div>
  );
}
