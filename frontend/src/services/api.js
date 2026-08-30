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

export const api = {
  // System Health
  async getHealth() {
    const res = await fetch(`${API_BASE}/health`);
    return handleResponse(res);
  },

  // Customers
  async getCustomers() {
    const res = await fetch(`${API_BASE}/customers`);
    return handleResponse(res);
  },

  async getCustomer(id) {
    const res = await fetch(`${API_BASE}/customers/${encodeURIComponent(id)}`);
    return handleResponse(res);
  },

  async createCustomer(customer) {
    const res = await fetch(`${API_BASE}/customers`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(customer)
    });
    return handleResponse(res);
  },

  async updateCustomer(id, customer) {
    const res = await fetch(`${API_BASE}/customers/${encodeURIComponent(id)}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(customer)
    });
    return handleResponse(res);
  },

  async deleteCustomer(id) {
    const res = await fetch(`${API_BASE}/customers/${encodeURIComponent(id)}`, {
      method: 'DELETE'
    });
    return handleResponse(res);
  },

  // Vehicles
  async getVehicles() {
    const res = await fetch(`${API_BASE}/vehicles`);
    return handleResponse(res);
  },

  async getVehicle(id) {
    const res = await fetch(`${API_BASE}/vehicles/${encodeURIComponent(id)}`);
    return handleResponse(res);
  },

  async createVehicle(vehicle) {
    const res = await fetch(`${API_BASE}/vehicles`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(vehicle)
    });
    return handleResponse(res);
  },

  async updateVehicle(id, vehicle) {
    const res = await fetch(`${API_BASE}/vehicles/${encodeURIComponent(id)}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(vehicle)
    });
    return handleResponse(res);
  },

  async deleteVehicle(id) {
    const res = await fetch(`${API_BASE}/vehicles/${encodeURIComponent(id)}`, {
      method: 'DELETE'
    });
    return handleResponse(res);
  },

  // Depots
  async getDepots() {
    const res = await fetch(`${API_BASE}/depots`);
    return handleResponse(res);
  },

  async getDepot(id) {
    const res = await fetch(`${API_BASE}/depots/${encodeURIComponent(id)}`);
    return handleResponse(res);
  },

  async createDepot(depot) {
    const res = await fetch(`${API_BASE}/depots`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(depot)
    });
    return handleResponse(res);
  },

  async updateDepot(id, depot) {
    const res = await fetch(`${API_BASE}/depots/${encodeURIComponent(id)}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(depot)
    });
    return handleResponse(res);
  },

  async deleteDepot(id) {
    const res = await fetch(`${API_BASE}/depots/${encodeURIComponent(id)}`, {
      method: 'DELETE'
    });
    return handleResponse(res);
  },

  // Optimization
  async runOptimization(request) {
    const res = await fetch(`${API_BASE}/optimization/run`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request)
    });
    return handleResponse(res);
  },

  async getOptimization(id) {
    const res = await fetch(`${API_BASE}/optimization/${encodeURIComponent(id)}`);
    return handleResponse(res);
  },

  async getOptimizationHistory(status = null, limit = 50) {
    let url = `${API_BASE}/optimization`;
    const params = new URLSearchParams();
    if (status) params.append('status', status);
    if (limit) params.append('limit', limit);
    if (params.toString()) url += `?${params.toString()}`;

    const res = await fetch(url);
    return handleResponse(res);
  },

  // Traffic
  async updateTraffic(request) {
    const res = await fetch(`${API_BASE}/traffic/update`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request)
    });
    return handleResponse(res);
  },

  async reoptimize(optimizationId, trafficUpdate) {
    const res = await fetch(`${API_BASE}/optimization/${encodeURIComponent(optimizationId)}/reoptimize`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(trafficUpdate)
    });
    return handleResponse(res);
  }
};
