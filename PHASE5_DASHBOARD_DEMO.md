# Phase 5 — Fleet Optimization Dashboard Demo Guide

## Problem Statement 137: Quantum-Inspired Intelligent Traffic Route Optimization

This document outlines the step-by-step verification and demonstration workflow of the interactive Web Dashboard built for **Problem Statement 137**.

---

## 1. Quick Start & Execution

### 1.1 Start the Java REST API Backend
```powershell
# In the repository root
javac -d out src\com\routeoptimizer\*.java
java -cp out com.routeoptimizer.RestApiServer
```
*The embedded server initializes on port `8080` (or configured port) and hosts the complete fleet optimization REST API.*

### 1.2 Start the React + Vite Dashboard
```powershell
cd frontend
npm install
npm run dev
```
*Access the interactive dashboard in your browser at `http://localhost:5173`.*

---

## 2. Interactive Problem Statement 137 Demonstration Workflow

### Step 1: System Status & Fleet Verification
- **Header Status Pill**: Displays backend status `ONLINE`, along with active routing engine (`OSRM Real Road Network`) and traffic mode (`SIMULATED / LIVE`).
- **Fleet Hubs (Depots)**: View multiple depots (e.g. `North London Hub W1`, `South London Hub W2`).
- **Fleet Vehicles**: View vehicle fleet with capacities, fuel consumption rates, and stationed home depots.
- **Delivery Destinations (Customers)**: View destinations with real geographic coordinates, demands (kg), delivery priority badges (`HIGH`, `MEDIUM`, `LOW`), and time windows (`[08:00 - 12:00]`).

---

### Step 2: Run QIGA Joint Fleet Optimization
1. In the **QIGA Optimization Control** panel, set:
   - Population Size: `30`
   - Generations: `50`
   - Deterministic Seed: `42`
2. Click **"🚀 Run QIGA Optimization"**.
3. **Observation**:
   - The QIGA engine evaluates multi-vehicle permutations and vehicle assignments.
   - **Interactive Map**: Renders color-coded vehicle routes originating from home depots, visiting assigned customers in optimal sequence, and returning to the depot.
   - **Top KPI Cards**: Displays real-time calculations for:
     - `Total Distance` (km)
     - `Travel Time` (traffic-adjusted minutes)
     - `Fuel Consumption` (L)
     - `Transportation Cost` ($)
     - `QIGA Fitness Score`
     - `Constraint Validity` (`✓ 100% Valid`, 0 capacity & time-window violations)
   - **Vehicle Panel**: Shows capacity utilization bars (e.g., `78.0 / 80.0 kg (98%)`), total stops, route distance, and costs.

---

### Step 3: Interactive Route Filtering
- Click on any vehicle row (e.g., `V1` or `V2`) in the Fleet Vehicles tab or on the map polyline.
- **Observation**: The map highlights that specific vehicle's route and dims other vehicles, allowing dispatchers to isolate and inspect single vehicle tours.
- Click **"Show All Routes"** in the map header to restore full fleet visualization.

---

### Step 4: Inject Sudden Congestion Surge & Dynamic Re-Optimization
*This is the core Problem Statement 137 capability: handling unexpected traffic delays in real time.*

1. Click **"🚨 Simulate Traffic Surge"** in the top navigation bar or Traffic Panel.
2. In the modal:
   - Select Origin: `W1` (North London Hub)
   - Select Destination: `C1` (Westminster)
   - Set Congestion Multiplier: `3.0x` (Severe Gridlock)
3. Click **"⚡ Inject Congestion & Re-Optimize Fleet"**.
4. **Observation**:
   - The traffic update is posted to `POST /api/v1/traffic/update`.
   - The dynamic fleet re-optimizer evaluates affected routes.
   - **Before vs After Comparison Box**:
     - Displays Plan A (Initial Run) vs Plan B (Re-optimized Revision).
     - Compares distance, time, fuel, cost, and fitness score side-by-side.
     - Demonstrates vehicle route adjustments that detour around the congested segment.
     - Confirms that previously serviced stops remain protected without disruption.

---

### Step 5: Optimization History & Audit Trail
1. Switch to the **📜 Optimization History** tab.
2. **Observation**:
   - Both the initial run (`opt-xxxxxxxx`) and the re-optimized revision (`opt-xxxxxxxx-revXXX`) are permanently logged.
   - Click **"View Plan"** on any past record to instantly reconstruct that run's complete routes and metrics on the map and KPI cards.
