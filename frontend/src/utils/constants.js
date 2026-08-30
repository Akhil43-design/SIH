/**
 * Color palettes, markers, constants, and Indian localization formatters for Fleet Visualization
 */

export const BENGALURU_CENTER = [12.9716, 77.5946];

export const VEHICLE_COLORS = [
  '#8b5cf6', // Violet / Purple (V1)
  '#3b82f6', // Blue (V2)
  '#10b981', // Emerald / Green (V3)
  '#f59e0b', // Amber / Orange
  '#ec4899', // Pink
  '#06b6d4', // Cyan
  '#14b8a6', // Teal
  '#f43f5e'  // Rose
];

export const PRIORITY_COLORS = {
  HIGH: '#ef4444',   // Red
  MEDIUM: '#f59e0b', // Amber / Orange (Matches mockup)
  LOW: '#10b981'     // Green
};

export const STATUS_COLORS = {
  COMPLETED: '#10b981',
  RUNNING: '#3b82f6',
  QUEUED: '#f59e0b',
  FAILED: '#ef4444',
  PENDING: '#64748b',
  IN_ROUTE: '#06b6d4',
  CANCELLED: '#94a3b8'
};

export const VEHICLE_TYPES = {
  V1: 'Mini Truck',
  V2: 'Delivery Van',
  V3: 'Light Commercial Vehicle',
  DEFAULT: 'Commercial Vehicle'
};

export function getVehicleType(vehicleId) {
  return VEHICLE_TYPES[vehicleId] || VEHICLE_TYPES.DEFAULT;
}

export function getVehicleColor(index) {
  return VEHICLE_COLORS[index % VEHICLE_COLORS.length];
}

export function formatCurrencyINR(value) {
  if (value == null || isNaN(value)) return '₹0.00';
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 2,
    minimumFractionDigits: 2
  }).format(value);
}

export function formatTime(minutes) {
  if (minutes == null || isNaN(minutes)) return '--';
  const hrs = Math.floor(minutes / 60);
  const mins = Math.round(minutes % 60);
  if (hrs === 0) return `${mins}m`;
  return `${hrs}h ${mins}m`;
}

export function formatClockTime(minutesFromMidnight) {
  if (minutesFromMidnight == null || isNaN(minutesFromMidnight)) return '--:--';
  const totalMins = Math.round(minutesFromMidnight) % 1440;
  const hrs = Math.floor(totalMins / 60);
  const mins = totalMins % 60;
  return `${String(hrs).padStart(2, '0')}:${String(mins).padStart(2, '0')}`;
}

export function formatISTTime(date = new Date()) {
  const d = (date instanceof Date) ? date : new Date(date);
  return d.toLocaleTimeString('en-IN', {
    timeZone: 'Asia/Kolkata',
    hour: '2-digit',
    minute: '2-digit',
    hour12: true
  }) + ' IST';
}

export function formatISTDateTime(date = new Date()) {
  const d = (date instanceof Date) ? date : new Date(date);
  const options = {
    timeZone: 'Asia/Kolkata',
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: true
  };
  return d.toLocaleString('en-IN', options) + ' IST';
}
