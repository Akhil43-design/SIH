import {
  formatTime,
  formatClockTime,
  getVehicleColor,
  getVehicleType,
  formatCurrencyINR,
  formatISTTime,
  formatISTDateTime,
  BENGALURU_CENTER
} from '../src/utils/constants.js';

import { fetchOSRMRouteGeometry } from '../src/services/api.js';

console.log('========================================');
console.log('  PHASE 5C FRONTEND DASHBOARD TESTS');
console.log('========================================\n');

let passed = 0;
let failed = 0;

function assert(condition, message) {
  if (condition) {
    console.log(`[PASS] ${message}`);
    passed++;
  } else {
    console.error(`[FAIL] ${message}`);
    failed++;
  }
}

async function runTests() {
  // 1. Time formatting tests
  assert(formatTime(45) === '45m', 'formatTime(45) -> 45m');
  assert(formatTime(125) === '2h 5m', 'formatTime(125) -> 2h 5m');
  assert(formatTime(null) === '--', 'formatTime(null) -> --');

  // 2. Clock formatting tests
  assert(formatClockTime(60) === '01:00', 'formatClockTime(60) -> 01:00');
  assert(formatClockTime(540) === '09:00', 'formatClockTime(540) -> 09:00');
  assert(formatClockTime(1050) === '17:30', 'formatClockTime(1050) -> 17:30');

  // 3. Indian Currency (₹ INR) tests
  const inr1 = formatCurrencyINR(1416.41);
  assert(inr1.includes('1,416.41') || inr1.includes('1416.41'), `formatCurrencyINR(1416.41) contains formatted amount: ${inr1}`);
  const inrNull = formatCurrencyINR(null);
  assert(inrNull === '₹0.00' || inrNull.includes('0.00'), 'formatCurrencyINR(null) -> ₹0.00');

  // 4. Indian Standard Time (IST) tests
  const istTime = formatISTTime(new Date());
  assert(istTime.includes('IST'), `formatISTTime contains 'IST': ${istTime}`);
  const istDateTime = formatISTDateTime(new Date());
  assert(istDateTime.includes('IST'), `formatISTDateTime contains 'IST': ${istDateTime}`);

  // 5. Vehicle Colors & Types
  assert(getVehicleColor(0) === '#8b5cf6', 'getVehicleColor(0) -> Violet');
  assert(getVehicleColor(1) === '#3b82f6', 'getVehicleColor(1) -> Blue');
  assert(getVehicleType('V1') === 'Mini Truck', 'getVehicleType(V1) -> Mini Truck');
  assert(getVehicleType('V2') === 'Delivery Van', 'getVehicleType(V2) -> Delivery Van');
  assert(getVehicleType('V3') === 'Light Commercial Vehicle', 'getVehicleType(V3) -> Light Commercial Vehicle');

  // 6. Bengaluru Default Center Coordinates
  assert(Array.isArray(BENGALURU_CENTER) && BENGALURU_CENTER.length === 2, 'BENGALURU_CENTER is a [lat, lng] array');
  assert(Math.abs(BENGALURU_CENTER[0] - 12.9716) < 0.001, 'BENGALURU_CENTER Latitude is ~12.9716');
  assert(Math.abs(BENGALURU_CENTER[1] - 77.5946) < 0.001, 'BENGALURU_CENTER Longitude is ~77.5946');

  // 7. OSRM Road Geometry Multi-Stop Fetching
  const demoWaypoints = [
    [12.9978, 77.5587], // Peenya
    [13.0475, 77.6200], // Manyata
    [12.9978, 77.5587]  // Peenya
  ];

  const emptyCoords = await fetchOSRMRouteGeometry([]);
  assert(Array.isArray(emptyCoords) && emptyCoords.length === 0, 'fetchOSRMRouteGeometry([]) returns empty array');

  const roadGeometry = await fetchOSRMRouteGeometry(demoWaypoints);
  assert(Array.isArray(roadGeometry), 'fetchOSRMRouteGeometry returns array of coordinates');
  assert(roadGeometry.length >= demoWaypoints.length, `OSRM road points count (${roadGeometry.length}) >= waypoints count (${demoWaypoints.length})`);
  assert(roadGeometry.length > 2, `OSRM returns rich intermediate road curves (Points: ${roadGeometry.length})`);

  // 8. OSRM Geometry Cache Hit Test
  const cachedGeometry = await fetchOSRMRouteGeometry(demoWaypoints);
  assert(cachedGeometry === roadGeometry, 'Identical waypoint query successfully retrieved from in-memory cache');

  console.log('\n========================================');
  console.log(`SUMMARY: ${passed} PASSED, ${failed} FAILED`);
  console.log('========================================');

  if (failed > 0) {
    process.exit(1);
  }
}

runTests();
