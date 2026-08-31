/**
 * Centralized API Service for QuantumRouteOptimizer
 * Problem Statement 137
 */

const API_BASE = '/api/v1';

async function handleResponse(response) {
  if (response.status === 204) {
    return true;
  }
  const contentType = response.headers.get('content-type');
  let data;
  if (contentType && contentType.includes('application/json')) {
    data = await response.json();
  } else {
    data = await response.text();
  }

  if (!response.ok) {
    const errorMsg = data && data.message ? data.message : `HTTP ${response.status}: ${response.statusText}`;
    const err = new Error(errorMsg);
    err.status = response.status;
    err.data = data;
    throw err;
  }
  return data;
}

// System Health
export async function getHealth() {
  const res = await fetch(`${API_BASE}/health`);
  return handleResponse(res);
}

// Customers
export async function getCustomers() {
  const res = await fetch(`${API_BASE}/customers`);
  return handleResponse(res);
}

export async function getCustomer(id) {
  const res = await fetch(`${API_BASE}/customers/${encodeURIComponent(id)}`);
  return handleResponse(res);
}

export async function createCustomer(customer) {
  const res = await fetch(`${API_BASE}/customers`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(customer)
  });
  return handleResponse(res);
}

export async function updateCustomer(id, customer) {
  const res = await fetch(`${API_BASE}/customers/${encodeURIComponent(id)}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(customer)
  });
  return handleResponse(res);
}

export async function deleteCustomer(id) {
  const res = await fetch(`${API_BASE}/customers/${encodeURIComponent(id)}`, {
    method: 'DELETE'
  });
  return handleResponse(res);
}

// Vehicles
export async function getVehicles() {
  const res = await fetch(`${API_BASE}/vehicles`);
  return handleResponse(res);
}

export async function getVehicle(id) {
  const res = await fetch(`${API_BASE}/vehicles/${encodeURIComponent(id)}`);
  return handleResponse(res);
}

export async function createVehicle(vehicle) {
  const res = await fetch(`${API_BASE}/vehicles`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(vehicle)
  });
  return handleResponse(res);
}

export async function updateVehicle(id, vehicle) {
  const res = await fetch(`${API_BASE}/vehicles/${encodeURIComponent(id)}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(vehicle)
  });
  return handleResponse(res);
}

export async function deleteVehicle(id) {
  const res = await fetch(`${API_BASE}/vehicles/${encodeURIComponent(id)}`, {
    method: 'DELETE'
  });
  return handleResponse(res);
}

// Depots
export async function getDepots() {
  const res = await fetch(`${API_BASE}/depots`);
  return handleResponse(res);
}

export async function getDepot(id) {
  const res = await fetch(`${API_BASE}/depots/${encodeURIComponent(id)}`);
  return handleResponse(res);
}

export async function createDepot(depot) {
  const res = await fetch(`${API_BASE}/depots`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(depot)
  });
  return handleResponse(res);
}

export async function updateDepot(id, depot) {
  const res = await fetch(`${API_BASE}/depots/${encodeURIComponent(id)}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(depot)
  });
  return handleResponse(res);
}

export async function deleteDepot(id) {
  const res = await fetch(`${API_BASE}/depots/${encodeURIComponent(id)}`, {
    method: 'DELETE'
  });
  return handleResponse(res);
}

// Optimization Runs & Re-optimization
export async function runOptimization(request) {
  const res = await fetch(`${API_BASE}/optimization/run`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request)
  });
  return handleResponse(res);
}

export async function getOptimizationHistory() {
  const res = await fetch(`${API_BASE}/optimization`);
  return handleResponse(res);
}

export async function getOptimizationResult(id) {
  const res = await fetch(`${API_BASE}/optimization/${encodeURIComponent(id)}`);
  return handleResponse(res);
}

export async function reoptimizePlan(id, trafficUpdate) {
  const res = await fetch(`${API_BASE}/optimization/${encodeURIComponent(id)}/reoptimize`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(trafficUpdate)
  });
  return handleResponse(res);
}

// Traffic Updates & Status
export async function updateTraffic(trafficUpdate) {
  const res = await fetch(`${API_BASE}/traffic/update`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(trafficUpdate)
  });
  return handleResponse(res);
}

// OSRM Multi-Stop Road Geometry Service with In-Memory Caching
const osrmGeometryCache = new Map();

export async function fetchOSRMRouteGeometry(waypoints) {
  if (!waypoints || waypoints.length < 2) return [];

  const key = waypoints.map(([lat, lng]) => `${Number(lat).toFixed(6)},${Number(lng).toFixed(6)}`).join(';');
  if (osrmGeometryCache.has(key)) {
    return osrmGeometryCache.get(key);
  }

  const coordsString = waypoints.map(([lat, lng]) => `${Number(lng).toFixed(6)},${Number(lat).toFixed(6)}`).join(';');
  const url = `https://router.project-osrm.org/route/v1/driving/${coordsString}?overview=full&geometries=geojson`;

  try {
    const res = await fetch(url);
    if (!res.ok) throw new Error(`OSRM HTTP ${res.status}`);
    const data = await res.json();
    if (data.code === 'Ok' && data.routes && data.routes.length > 0) {
      const geojsonCoords = data.routes[0].geometry.coordinates; // [[lon, lat], ...]
      const leafletCoords = geojsonCoords.map(([lon, lat]) => [lat, lon]);
      osrmGeometryCache.set(key, leafletCoords);
      return leafletCoords;
    }
  } catch (err) {
    console.warn('OSRM road geometry fetch note:', err.message);
  }

  // Fallback to direct waypoint polyline
  return waypoints;
}

export async function getCities() {
  const res = await fetch(`${API_BASE}/cities`);
  return handleResponse(res);
}

export async function selectCityDataset(cityId) {
  const res = await fetch(`${API_BASE}/cities/${encodeURIComponent(cityId)}/load`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({})
  });
  return handleResponse(res);
}

export const api = {
  getHealth,
  getCities,
  selectCityDataset,
  getCustomers,
  getCustomer,
  createCustomer,
  updateCustomer,
  deleteCustomer,
  getVehicles,
  getVehicle,
  createVehicle,
  updateVehicle,
  deleteVehicle,
  getDepots,
  getDepot,
  createDepot,
  updateDepot,
  deleteDepot,
  runOptimization,
  getOptimizationHistory,
  getOptimizationResult,
  reoptimizePlan,
  updateTraffic,
  fetchOSRMRouteGeometry
};

export default api;
