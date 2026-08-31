============================================================
PHASE 7B LOCATION PICKER REPORT
============================================================

Problem Statement: 26137
Title: Quantum-Inspired Intelligent Traffic Route Optimization in Transportation Systems Using Metaheuristic Optimization
Organization: Egreen Quanta

------------------------------------------------------------
ARCHITECTURE
------------------------------------------------------------
Replaced manual geographic coordinate entry with a Human-Friendly `LocationPicker` component. 
The new component seamlessly handles searching real-world Indian smart-city locations, resolving them into accurate `(latitude, longitude)` coordinates required by the Quantum-Inspired Genetic Algorithm (QIGA).

------------------------------------------------------------
GEOCODING PROVIDER
------------------------------------------------------------
Provider: OpenStreetMap Nominatim
Implementation: Client-side fetch with `countrycodes=in` constraints.
Throttling: 600ms input debounce.
Attribution: "Geocoding by OSM Nominatim" clearly displayed below the inline map.

------------------------------------------------------------
LOCATION SEARCH FLOW
------------------------------------------------------------
1. User clicks "Add Customer" or "Add Depot".
2. Type an address (e.g., "Electronic City Bengaluru").
3. Debounced API fetch returns matching POIs from India.
4. User selects a POI.
5. LocationPicker stores coordinates and updates the interactive inline Leaflet map.
6. The User can drag the marker in the inline map to fine-tune the exact drop-off point.
7. Saving the form persists exact geographic coordinates, leaving existing QIGA logic untouched.

------------------------------------------------------------
INDIAN CITY VALIDATION
------------------------------------------------------------
The selected POI `display_name` is strictly validated against the currently active dashboard city (e.g., "Bengaluru").
If the user selects "Andheri Mumbai" while the "Bengaluru" dataset is active, a warning explicitly prevents cross-city contamination, ensuring pure and physically realistic VRP boundaries.

------------------------------------------------------------
FALLBACK BEHAVIOR
------------------------------------------------------------
If Nominatim rate-limits the user, or network access drops:
- System does NOT crash.
- Error displays: "Location search unavailable. Try manual entry."
- User can toggle "Enter coordinates manually" which reveals numeric Latitude/Longitude inputs.

------------------------------------------------------------
SECURITY
------------------------------------------------------------
- OpenStreetMap Nominatim requires no explicit API Key.
- A custom `User-Agent` (QuantumRouteOptimizer-SIHDemo/1.0) is supplied per Nominatim ToS to prevent blocking.

------------------------------------------------------------
TESTS
------------------------------------------------------------
- Existing regression tests (37/37): PASS
- Phase 7B Management Tests: 43/43 PASS (Including 12 new UX/Geocoder rules)
- OSRM Routes: PASS
- QIGA Mathematical Execution: PASS (Completely untouched)

------------------------------------------------------------
SIH 26137 RELEVANCE
------------------------------------------------------------
A real-world dispatch operator does not inherently know the latitude/longitude of a new warehouse. This UX upgrade demonstrates true Enterprise-Readiness, converting the theoretical QIGA model into a deployable logistics product for Smart-City ecosystems without compromising the rigorous metaheuristic optimization engine.

============================================================
