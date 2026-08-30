import React, { useState, useEffect, useCallback } from 'react';
import { api } from './services/api';
import { Header } from './components/Header';
import { KpiMetrics } from './components/KpiMetrics';
import { FleetMap } from './components/FleetMap';
import { OptimizationPanel } from './components/OptimizationPanel';
import { TrafficPanel } from './components/TrafficPanel';
import { VehiclePanel } from './components/VehiclePanel';
import { CustomerPanel } from './components/CustomerPanel';
import { DepotPanel } from './components/DepotPanel';
import { OptimizationHistory } from './components/OptimizationHistory';
import { DynamicReoptModal } from './components/DynamicReoptModal';

export function App() {
  const [health, setHealth] = useState(null);
  const [customers, setCustomers] = useState([]);
  const [vehicles, setVehicles] = useState([]);
  const [depots, setDepots] = useState([]);
  const [optimization, setOptimization] = useState(null);
  const [history, setHistory] = useState([]);

  const [selectedVehicleId, setSelectedVehicleId] = useState(null);
  const [activeTab, setActiveTab] = useState('vehicles'); // 'vehicles' | 'customers' | 'depots' | 'history'

  const [isRefreshing, setIsRefreshing] = useState(false);
  const [isOptimizing, setIsOptimizing] = useState(false);
  const [isReoptimizing, setIsReoptimizing] = useState(false);
  const [trafficModalOpen, setTrafficModalOpen] = useState(false);
  const [reoptResult, setReoptResult] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);

  // Load all initial data from REST API
  const refreshData = useCallback(async () => {
    setIsRefreshing(true);
    setErrorMessage(null);
    try {
      // 1. Fetch Health
      try {
        const h = await api.getHealth();
        setHealth(h);
      } catch (err) {
        setHealth({ status: 'DOWN', message: err.message });
      }

      // 2. Fetch Fleet Data
      const [custs, vehs, deps, hist] = await Promise.all([
        api.getCustomers().catch(() => []),
        api.getVehicles().catch(() => []),
        api.getDepots().catch(() => []),
        api.getOptimizationHistory().catch(() => [])
      ]);

      setCustomers(custs || []);
      setVehicles(vehs || []);
      setDepots(deps || []);
      setHistory(hist || []);

      // If we have history and no active optimization loaded, fetch the latest run
      if (hist && hist.length > 0 && !optimization) {
        try {
          const latestRun = await api.getOptimization(hist[0].id);
          setOptimization(latestRun);
        } catch (e) {
          console.warn('Could not load latest run details', e);
        }
      }
    } catch (err) {
      setErrorMessage(`Failed to load fleet data from backend: ${err.message}`);
    } finally {
      setIsRefreshing(false);
    }
  }, [optimization]);

  useEffect(() => {
    refreshData();
  }, []);

  // Run Optimization
  const handleRunOptimization = async (params) => {
    setIsOptimizing(true);
    setErrorMessage(null);
    setReoptResult(null);
    try {
      const request = {
        depots,
        vehicles,
        customers: (customers || []).filter((c) => !c.cancelled),
        ...params
      };

      const result = await api.runOptimization(request);
      setOptimization(result);
      // Refresh history list
      const updatedHist = await api.getOptimizationHistory();
      setHistory(updatedHist || []);
    } catch (err) {
      setErrorMessage(`Optimization failed: ${err.message}`);
    } finally {
      setIsOptimizing(false);
    }
  };

  // Trigger Dynamic Traffic Surge & Re-optimization
  const handleTriggerReoptimization = async (trafficUpdate) => {
    if (!optimization) {
      setErrorMessage('Please run an initial optimization first before injecting traffic updates.');
      return;
    }

    setIsReoptimizing(true);
    setErrorMessage(null);
    try {
      // 1. Send Traffic Update
      await api.updateTraffic(trafficUpdate);

      // 2. Trigger Re-optimization
      const reoptResp = await api.reoptimize(optimization.optimizationId, trafficUpdate);
      setReoptResult(reoptResp);
      setOptimization(reoptResp); // Update active plan on map

      // Refresh history
      const updatedHist = await api.getOptimizationHistory();
      setHistory(updatedHist || []);
    } catch (err) {
      setErrorMessage(`Dynamic re-optimization failed: ${err.message}`);
    } finally {
      setIsReoptimizing(false);
    }
  };

  // Load a specific historical run
  const handleSelectHistoricalRun = async (optId) => {
    setIsRefreshing(true);
    try {
      const run = await api.getOptimization(optId);
      setOptimization(run);
      setReoptResult(null);
    } catch (err) {
      setErrorMessage(`Failed to fetch optimization ${optId}: ${err.message}`);
    } finally {
      setIsRefreshing(false);
    }
  };

  // Customer Actions
  const handleAddCustomer = async (cust) => {
    try {
      await api.createCustomer(cust);
      await refreshData();
    } catch (err) {
      setErrorMessage(`Failed to create customer: ${err.message}`);
    }
  };

  const handleCancelCustomer = async (id) => {
    try {
      const existing = customers.find((c) => c.id === id);
      if (existing) {
        await api.updateCustomer(id, { ...existing, cancelled: true });
        await refreshData();
      }
    } catch (err) {
      setErrorMessage(`Failed to cancel customer: ${err.message}`);
    }
  };

  // Depot Actions
  const handleAddDepot = async (depot) => {
    try {
      await api.createDepot(depot);
      await refreshData();
    } catch (err) {
      setErrorMessage(`Failed to create depot: ${err.message}`);
    }
  };

  return (
    <div className="app-container">
      {/* Header */}
      <Header
        health={health}
        isRefreshing={isRefreshing}
        onRefresh={refreshData}
        onOpenTrafficModal={() => setTrafficModalOpen(true)}
      />

      {/* Error Alert */}
      {errorMessage && (
        <div style={{
          backgroundColor: 'rgba(244, 63, 94, 0.15)',
          border: '1px solid rgba(244, 63, 94, 0.4)',
          color: 'var(--accent-rose)',
          padding: '12px 18px',
          borderRadius: '8px',
          fontSize: '13px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          <span>⚠️ {errorMessage}</span>
          <button
            onClick={() => setErrorMessage(null)}
            style={{ background: 'transparent', border: 'none', color: 'inherit', cursor: 'pointer' }}
          >
            ✕
          </button>
        </div>
      )}

      {/* Top KPI Metrics Cards */}
      <KpiMetrics
        optimization={optimization}
        totalVehicles={(vehicles || []).length}
        totalCustomers={(customers || []).length}
        totalDepots={(depots || []).length}
      />

      {/* Main Grid: Interactive Map + Control Panels */}
      <div className="dashboard-grid">
        {/* Left: Interactive Fleet Map */}
        <FleetMap
          depots={depots}
          customers={customers}
          optimization={optimization}
          selectedVehicleId={selectedVehicleId}
          onSelectVehicle={setSelectedVehicleId}
        />

        {/* Right: Optimization & Traffic Control */}
        <div className="control-column">
          <OptimizationPanel
            onRunOptimization={handleRunOptimization}
            isOptimizing={isOptimizing}
            lastOptimization={optimization}
          />

          <TrafficPanel
            health={health}
            onOpenTrafficModal={() => setTrafficModalOpen(true)}
          />
        </div>
      </div>

      {/* Bottom Tabs & Details: Vehicles, Customers, Depots, History */}
      <div className="panel-card" style={{ marginTop: '4px' }}>
        <div className="tab-nav">
          <button
            className={`tab-btn ${activeTab === 'vehicles' ? 'active' : ''}`}
            onClick={() => setActiveTab('vehicles')}
          >
            🚛 Fleet Vehicles ({(vehicles || []).length})
          </button>
          <button
            className={`tab-btn ${activeTab === 'customers' ? 'active' : ''}`}
            onClick={() => setActiveTab('customers')}
          >
            📍 Delivery Destinations ({(customers || []).length})
          </button>
          <button
            className={`tab-btn ${activeTab === 'depots' ? 'active' : ''}`}
            onClick={() => setActiveTab('depots')}
          >
            🏭 Depot Hubs ({(depots || []).length})
          </button>
          <button
            className={`tab-btn ${activeTab === 'history' ? 'active' : ''}`}
            onClick={() => setActiveTab('history')}
          >
            📜 Optimization History ({(history || []).length})
          </button>
        </div>

        {activeTab === 'vehicles' && (
          <VehiclePanel
            vehicles={vehicles}
            optimization={optimization}
            selectedVehicleId={selectedVehicleId}
            onSelectVehicle={setSelectedVehicleId}
          />
        )}

        {activeTab === 'customers' && (
          <CustomerPanel
            customers={customers}
            optimization={optimization}
            onAddCustomer={handleAddCustomer}
            onCancelCustomer={handleCancelCustomer}
          />
        )}

        {activeTab === 'depots' && (
          <DepotPanel
            depots={depots}
            vehicles={vehicles}
            onAddDepot={handleAddDepot}
          />
        )}

        {activeTab === 'history' && (
          <OptimizationHistory
            history={history}
            currentOptId={optimization?.optimizationId}
            onSelectRun={handleSelectHistoricalRun}
          />
        )}
      </div>

      {/* Dynamic Re-Optimization & Traffic Surge Modal */}
      <DynamicReoptModal
        isOpen={trafficModalOpen}
        onClose={() => setTrafficModalOpen(false)}
        depots={depots}
        customers={customers}
        lastOptimization={optimization}
        onTriggerReoptimization={handleTriggerReoptimization}
        reoptResult={reoptResult}
        isProcessing={isReoptimizing}
      />
    </div>
  );
}
