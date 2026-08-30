# Phase 5 — Web Dashboard & Interactive Fleet Map Report

## Problem Statement 137: Quantum-Inspired Intelligent Traffic Route Optimization

**Author:** Antigravity AI Engine  
**Project:** QuantumRouteOptimizer  
**GitHub Repository:** `https://github.com/Akhil43-design/SIH`  
**Phase:** 5 (Web Dashboard & Interactive Fleet Map)  
**Status:** COMPLETE & VERIFIED  

---

## 1. Executive Summary

Phase 5 completes the full stack architecture for Problem Statement 137 by delivering an interactive, web-based fleet logistics dashboard on top of the existing REST API and persistent database layer. 

The dashboard provides dispatchers with an interactive geographic interface for multi-vehicle, multi-depot fleet management, real-time traffic visualization, one-click QIGA optimization, and instant dynamic re-optimization during sudden congestion surges.

### Core Architectural Separation:
- **No Optimization in Frontend**: The frontend strictly serves as a visualization and dispatch control plane.
- **Backend as Single Source of Truth**: All QIGA mathematical evaluations, multi-objective trade-offs, constraint enforcements, and OSRM routing calculations remain authoritative in the Java backend.
- **Audited Dynamic Revisions**: Dynamic re-routing preserves historical runs in relational storage while displaying before/after metric comparisons.

---

## 2. Frontend Architecture & Technology Stack

- **Framework**: React 18 + Vite 5
- **Mapping Engine**: Leaflet 1.9 + CartoDB Dark Matter / Voyager Geographic Tiles
- **Styling**: Modern Vanilla CSS Design System with dark-mode glassmorphism cards, glowing status indicators, responsive data tables, and dynamic progress bars.
- **State & Service Layer**: Centralized API Client (`src/services/api.js`) consuming REST endpoints (`/api/v1/health`, `/api/v1/customers`, `/api/v1/vehicles`, `/api/v1/depots`, `/api/v1/optimization`, `/api/v1/traffic`).

```
┌────────────────────────────────────────────────────────┐
│                   REACT DASHBOARD                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Header     │  │  KPI Cards   │  │  Fleet Map   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ QIGA Control │  │Traffic Surge │  │ Fleet Tabs   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└───────────────────────────┬────────────────────────────┘
                            │ REST API (JSON / HTTP)
┌───────────────────────────▼────────────────────────────┐
│                    JAVA REST API                       │
│  (RestApiServer / Controllers / DTOs / Validation)     │
└───────────────────────────┬────────────────────────────┘
                            │ Service Invocations
┌───────────────────────────▼────────────────────────────┐
│                  QIGA OPTIMIZATION                     │
│  (MultiVehicleQIGA / DynamicFleetOptimizer / OSRM)     │
└───────────────────────────┬────────────────────────────┘
                            │ Repositories / Transactions
┌───────────────────────────▼────────────────────────────┐
│                  PERSISTENT STORAGE                    │
│  (Depots, Vehicles, Customers, Runs, Routes, Traffic)  │
└────────────────────────────────────────────────────────┘
```

---

## 3. Interactive Map & Fleet Visualization

- **Real Geographic Coordinates**: Plots London depot hubs (King's Cross `[51.5308, -0.1238]`, Southwark `[51.5055, -0.0863]`) and delivery destinations with accurate latitudes and longitudes.
- **Priority-Coded Markers**: Distinct visual indicators for `HIGH` (Red), `MEDIUM` (Blue), and `LOW` (Green) delivery priorities, including demand payload and time windows.
- **Multi-Vehicle Route Polylines**: Distinct color schemes per vehicle (Cyan, Purple, Emerald, Amber, Rose) tracing the exact journey sequence: `Depot → Customer 1 → Customer 2 → ... → Depot`.
- **Interactive Tour Isolation**: Clicking any vehicle highlights its active route while dimming other fleet tours.

---

## 4. Problem Statement 137 Verification Matrix

| Capability Requirement | Dashboard Feature | Status |
|---|---|---|
| Multi-Vehicle Joint Optimization | Interactive Fleet Map & Multi-Vehicle Routes | **VERIFIED** |
| Multi-Depot Fleet Allocation | Multi-Hub Depot Markers & Home Depot Return Trails | **VERIFIED** |
| Time-Dependent Traffic Consideration | Diurnal Speed Multiplier Calculations & Adjustments | **VERIFIED** |
| Live / Fallback Traffic Status | Traffic Status Indicator Pill & Source Badge | **VERIFIED** |
| Dynamic Traffic Surge Simulation | Congestion Injection Modal (`updateTraffic`) | **VERIFIED** |
| Dynamic Fleet Re-Optimization | Real-Time Rerouting & Completed Stop Protection | **VERIFIED** |
| Multi-Objective Metrics (Fuel, Time, Cost) | 6 Live KPI Metric Cards & Before/After Comparison | **VERIFIED** |
| Vehicle Capacity Constraints | Visual Capacity Utilization Progress Bars | **VERIFIED** |
| Delivery Time Windows | Window Formatting (`[08:00 - 12:00]`) & Violation Count | **VERIFIED** |
| Delivery Priorities | Visual Priority Badges (`HIGH`, `MEDIUM`, `LOW`) | **VERIFIED** |
| Historical Run Audit Trail | Permanent Relational Storage & Run Recovery | **VERIFIED** |

---

## 5. Complete Test Verification Matrix (29 Backend + 8 Frontend = 37 Tests)

### 5.1 Java Backend Test Suites (29/29 Passed)
```
========================================================================================
#  Test Suite Class                         Focus Area                        Status
========================================================================================
1  FleetManagementControllerTest            Customer, Vehicle, Depot CRUD     PASSED
2  OptimizationControllerTest               QIGA API Execution & Re-Opt       PASSED
3  ApiEndToEndTest                          Embedded Server & HTTP Statuses   PASSED
4  PersistenceIntegrationTest               Relational Schema & FK Checks     PASSED
5  OptimizationPersistenceTest              Runs, Results & Stop Persistence  PASSED
6  DynamicPersistenceTest                   Dynamic Re-Opt Revisions & Audit  PASSED
7  PersistenceRestartTest                   Server Crash / Restart Recovery   PASSED
8  Phase5DashboardTest                      Full Dashboard API & Flow         PASSED
9  RepeatabilityTest                        Deterministic Seeded Execution    PASSED
10 QIGAvsBruteForce                         Exact Solution Optimality         PASSED
11 LargeDatasetComparison                   QIGA Scale Efficiency             PASSED
12 Step44EScalability                       Large Permutation Stability       PASSED
13 RealisticDatasetTest                     Multi-Constraint Synthetic        PASSED
14 FinalAlgorithmValidation                 3.6M Permutations Global Check    PASSED
15 MultiVehicleValidationTest               Fleet Constraints & Validation    PASSED
16 MultiVehicleQIGATest                     Joint Fleet Convergence           PASSED
17 MultiDepotValidationTest                 Multi-Depot Routing & Clustering  PASSED
18 TimeDependentTrafficTest                 Diurnal Traffic Curve Effect      PASSED
19 QIGAvsGABenchmark                        QIGA vs Classical GA Benchmark    PASSED
20 Phase2IntegrationTest                    End-to-End Phase 2 Integration    PASSED
21 OSRMRoutingTest                          OSRM Real Road Network API Client PASSED
22 RealGeographicDatasetTest                London Geographic Nodes           PASSED
23 RealGeographicFleetOptimizationTest      Real London Fleet Routing         PASSED
24 SyntheticVsRealRoutingTest               Circuity Ratio Verification       PASSED
25 LiveTrafficProviderTest                  Live Traffic Fallback Contract    PASSED
26 TrafficFallbackTest                      Strict vs Resilient Fallback      PASSED
27 DynamicReoptimizationTest                Dynamic Re-Opt & Stop Protection  PASSED
28 LiveVsSimulatedTrafficTest               Diurnal Congestion Curve Match    PASSED
29 DynamicTrafficFleetScenarioTest          Congestion Injection Scenario     PASSED
========================================================================================
```

### 5.2 Frontend Unit Tests (8/8 Passed)
```
[PASS] formatTime(45) -> 45m
[PASS] formatTime(125) -> 2h 5m
[PASS] formatTime(null) -> --
[PASS] formatClockTime(60) -> 01:00
[PASS] formatClockTime(540) -> 09:00
[PASS] formatClockTime(1050) -> 17:30
[PASS] getVehicleColor(0) -> Cyan
[PASS] getVehicleColor(1) -> Violet
```

### 5.3 Production Build
- **Vite Build**: Successfully compiled to `frontend/dist/` in 2.17 seconds.

---

## 6. Security Audit & Zero Secret Policy

- **No Secrets Committed**: Checked against git staging area.
- **Environment Isolation**: Live traffic credentials (`TRAFFIC_API_KEY`) and database passwords remain strictly on the backend server environment.
- **Safe Fallbacks**: Zero exposure of sensitive stack traces in API error responses.

---

## 7. Known Limitations

1. **Live Traffic API Credentials**: Real-time traffic querying requires setting `TRAFFIC_API_KEY` with TomTom/HERE credentials. When unset, the system gracefully falls back to diurnal sinusoidal speed modeling.
2. **GPS Stream Emulation**: Vehicle positions along active routes are evaluated discretely at stop milestones rather than continuous 1 Hz IoT telemetry.
3. **Authentication & Multi-Tenancy**: The current API is designed for internal logistics dispatch without role-based access control (RBAC).
