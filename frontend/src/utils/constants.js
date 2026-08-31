/**
 * Color palettes, markers, constants, and Indian localization formatters for Fleet Visualization
 */

export const BENGALURU_CENTER = [12.9716, 77.5946];

export const INDIAN_CITIES = [
  { id: 'bengaluru', name: 'Bengaluru', state: 'Karnataka', center: [12.9716, 77.5946], zoom: 11, desc: 'Silicon Valley Logistics Corridor' },
  { id: 'hyderabad', name: 'Hyderabad', state: 'Telangana', center: [17.3850, 78.4867], zoom: 11, desc: 'Cyberabad Tech & Logistics Zone' },
  { id: 'mumbai', name: 'Mumbai', state: 'Maharashtra', center: [19.0760, 72.8777], zoom: 11, desc: 'Financial Capital & Port Logistics Hub' },
  { id: 'delhi', name: 'Delhi NCR', state: 'Delhi', center: [28.6139, 77.2090], zoom: 11, desc: 'National Capital Region Freight Corridor' },
  { id: 'chennai', name: 'Chennai', state: 'Tamil Nadu', center: [13.0827, 80.2707], zoom: 11, desc: 'Automobile & Port Logistics Gateway' },
  { id: 'pune', name: 'Pune', state: 'Maharashtra', center: [18.5204, 73.8567], zoom: 11, desc: 'Auto Manufacturing & Tech Logistics Hub' },
  { id: 'kolkata', name: 'Kolkata', state: 'West Bengal', center: [22.5726, 88.3639], zoom: 11, desc: 'Eastern India Commercial Hub' },
  { id: 'ahmedabad', name: 'Ahmedabad', state: 'Gujarat', center: [23.0225, 72.5714], zoom: 11, desc: 'Textile & Industrial Freight Center' },
  { id: 'jaipur', name: 'Jaipur', state: 'Rajasthan', center: [26.9124, 75.7873], zoom: 11, desc: 'Pink City Industrial Logistics Gateway' },
  { id: 'kochi', name: 'Kochi', state: 'Kerala', center: [9.9312, 76.2673], zoom: 11, desc: 'Port City & Coastal IT Logistics Corridor' },
  { id: 'visakhapatnam', name: 'Visakhapatnam', state: 'Andhra Pradesh', center: [17.6868, 83.2185], zoom: 11, desc: 'Port City Logistics Hub' },
  { id: 'surat', name: 'Surat', state: 'Gujarat', center: [21.1702, 72.8311], zoom: 11, desc: 'Diamond City Transport Hub' },
  { id: 'lucknow', name: 'Lucknow', state: 'Uttar Pradesh', center: [26.8467, 80.9462], zoom: 11, desc: 'Nawab City Trade Route' },
  { id: 'indore', name: 'Indore', state: 'Madhya Pradesh', center: [22.7196, 75.8577], zoom: 11, desc: 'Cleanest City Distribution Hub' },
  { id: 'nagpur', name: 'Nagpur', state: 'Maharashtra', center: [21.1458, 79.0882], zoom: 11, desc: 'Zero Mile Freight Center' },
  { id: 'coimbatore', name: 'Coimbatore', state: 'Tamil Nadu', center: [11.0168, 76.9558], zoom: 11, desc: 'Manchester of South India' },
  { id: 'bhubaneswar', name: 'Bhubaneswar', state: 'Odisha', center: [20.2961, 85.8245], zoom: 11, desc: 'Temple City Regional Hub' },
  { id: 'chandigarh', name: 'Chandigarh', state: 'Chandigarh', center: [30.7333, 76.7794], zoom: 11, desc: 'Planned City Transport Hub' },
  { id: 'mysuru', name: 'Mysuru', state: 'Karnataka', center: [12.2958, 76.6394], zoom: 11, desc: 'Heritage City Delivery Belt' },
  { id: 'vijayawada', name: 'Vijayawada', state: 'Andhra Pradesh', center: [16.5062, 80.6480], zoom: 11, desc: 'Business Capital Hub' }
];

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
  MEDIUM: '#f59e0b', // Amber / Orange
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
  V1: 'Tata Ace Mini Truck',
  V2: 'Mahindra Bolero Maxi Truck',
  V3: 'Tata 407 LCV',
  V4: 'Ashok Leyland Dost',
  V5: 'Mahindra Jeeto',
  DEFAULT: 'Commercial Fleet Vehicle'
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
  try {
    const d = typeof date === 'string' || typeof date === 'number' ? new Date(date) : date;
    const timeStr = d.toLocaleTimeString('en-IN', {
      timeZone: 'Asia/Kolkata',
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    });
    return `${timeStr} IST`;
  } catch (e) {
    return '11:24 AM IST';
  }
}

export function formatISTDateTime(date = new Date()) {
  try {
    const d = typeof date === 'string' || typeof date === 'number' ? new Date(date) : date;
    const dateStr = d.toLocaleDateString('en-IN', {
      timeZone: 'Asia/Kolkata',
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
    const timeStr = d.toLocaleTimeString('en-IN', {
      timeZone: 'Asia/Kolkata',
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    });
    return `${dateStr}, ${timeStr} IST`;
  } catch (e) {
    return '30 May 2025, 11:23 AM IST';
  }
}
