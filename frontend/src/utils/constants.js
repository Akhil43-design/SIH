/**
 * Color palettes, markers, and constants for Fleet Visualization
 */

export const VEHICLE_COLORS = [
  '#06b6d4', // Cyan
  '#8b5cf6', // Violet / Purple
  '#10b981', // Emerald / Green
  '#f59e0b', // Amber / Orange
  '#ec4899', // Pink
  '#3b82f6', // Blue
  '#14b8a6', // Teal
  '#f43f5e'  // Rose
];

export const PRIORITY_COLORS = {
  HIGH: '#ef4444',   // Red
  MEDIUM: '#3b82f6', // Blue
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

export function getVehicleColor(index) {
  return VEHICLE_COLORS[index % VEHICLE_COLORS.length];
}

export function formatTime(minutes) {
  if (minutes == null) return '--';
  const hrs = Math.floor(minutes / 60);
  const mins = Math.round(minutes % 60);
  if (hrs === 0) return `${mins}m`;
  return `${hrs}h ${mins}m`;
}

export function formatClockTime(minutesFromMidnight) {
  if (minutesFromMidnight == null) return '--:--';
  const totalMins = Math.round(minutesFromMidnight) % 1440;
  const hrs = Math.floor(totalMins / 60);
  const mins = totalMins % 60;
  return `${String(hrs).padStart(2, '0')}:${String(mins).padStart(2, '0')}`;
}
