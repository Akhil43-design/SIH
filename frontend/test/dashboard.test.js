import { formatTime, formatClockTime, getVehicleColor } from '../src/utils/constants.js';

console.log('========================================');
console.log('     FRONTEND DASHBOARD UNIT TESTS');
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

// 1. Time formatting tests
assert(formatTime(45) === '45m', 'formatTime(45) -> 45m');
assert(formatTime(125) === '2h 5m', 'formatTime(125) -> 2h 5m');
assert(formatTime(null) === '--', 'formatTime(null) -> --');

// 2. Clock formatting tests
assert(formatClockTime(60) === '01:00', 'formatClockTime(60) -> 01:00');
assert(formatClockTime(540) === '09:00', 'formatClockTime(540) -> 09:00');
assert(formatClockTime(1050) === '17:30', 'formatClockTime(1050) -> 17:30');

// 3. Vehicle Colors
assert(getVehicleColor(0) === '#06b6d4', 'getVehicleColor(0) -> Cyan');
assert(getVehicleColor(1) === '#8b5cf6', 'getVehicleColor(1) -> Violet');

console.log('\n========================================');
console.log(`SUMMARY: ${passed} PASSED, ${failed} FAILED`);
console.log('========================================');

if (failed > 0) process.exit(1);
