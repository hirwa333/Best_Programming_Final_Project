// ===== SLTMS — Application Data & Logic =====

// Simulated in-memory database (mirrors Java backend)
const DB = {
  users: [
    { id: 'ADM-001', name: 'Hirwa Roy', email: 'admin@impano.rw', password: 'admin123', role: 'ADMIN' },
    { id: 'MGR-001', name: 'Manager One', email: 'manager@impano.rw', password: 'manager123', role: 'MANAGER' }
  ],
  clients: [
    { id: 'CLT-001', name: 'Kigali Traders Ltd', email: 'kigali@traders.rw', phone: '+250788001001', address: 'Kigali, Rwanda' },
    { id: 'CLT-002', name: 'Butare Supplies', email: 'butare@supplies.rw', phone: '+250788002002', address: 'Butare, Rwanda' },
    { id: 'CLT-003', name: 'Musanze Fresh Foods', email: 'musanze@fresh.rw', phone: '+250788003003', address: 'Musanze, Rwanda' }
  ],
  drivers: [
    { id: 'DRV-001', name: 'Jean Bosco', email: 'bosco@impano.rw', license: 'RW-DL-001', available: true },
    { id: 'DRV-002', name: 'Amina Uwase', email: 'amina@impano.rw', license: 'RW-DL-002', available: true },
    { id: 'DRV-003', name: 'Patrick Nkusi', email: 'patrick@impano.rw', license: 'RW-DL-003', available: false }
  ],
  vehicles: [
    { id: 'VEH-001', plate: 'RAC 001A', type: 'TRUCK',      capacity: 5000, status: 'AVAILABLE' },
    { id: 'VEH-002', plate: 'RAB 202B', type: 'VAN',        capacity: 1500, status: 'AVAILABLE' },
    { id: 'VEH-003', plate: 'RAD 303C', type: 'MOTORCYCLE', capacity: 100,  status: 'ON_TRIP'   }
  ],
  shipments: [
    { id: 'SHP-A1B2C3D4', origin: 'Kigali', destination: 'Butare',  weight: 200, distance: 130, status: 'DELIVERED',  client: 'CLT-001', driver: 'DRV-001', vehicle: 'VEH-001', cost: 24000 },
    { id: 'SHP-E5F6G7H8', origin: 'Kigali', destination: 'Musanze', weight: 50,  distance: 90,  status: 'IN_TRANSIT', client: 'CLT-002', driver: 'DRV-002', vehicle: 'VEH-002', cost: 12750 },
    { id: 'SHP-I9J0K1L2', origin: 'Butare', destination: 'Kigali',  weight: 10,  distance: 130, status: 'PENDING',   client: 'CLT-003', driver: null,      vehicle: null,      cost: 0     }
  ],
  invoices: [
    { id: 'INV-AA11BB22', shipmentId: 'SHP-A1B2C3D4', amount: 24000, paid: true  },
    { id: 'INV-CC33DD44', shipmentId: 'SHP-E5F6G7H8', amount: 12750, paid: false }
  ]
};

// Current logged-in user session
let currentUser = null;

// ===== AUTH =====
function login(email, password) {
  const user = DB.users.find(u => u.email === email && u.password === password);
  if (user) { currentUser = user; return true; }
  return false;
}

function logout() {
  currentUser = null;
  window.location.href = 'index.html';
}

function requireAuth() {
  const saved = sessionStorage.getItem('sltms_user');
  if (saved) { currentUser = JSON.parse(saved); return true; }
  window.location.href = 'index.html';
  return false;
}

function saveSession(user) {
  sessionStorage.setItem('sltms_user', JSON.stringify(user));
}

// ===== COST CALCULATION (Strategy Pattern) =====
function calculateCost(vehicleType, distanceKm, weightKg) {
  switch (vehicleType) {
    case 'TRUCK':      return 5000 + (distanceKm * 150) + (weightKg * 20);
    case 'VAN':        return 3000 + (distanceKm * 100) + (weightKg * 15);
    case 'MOTORCYCLE': return 1000 + (distanceKm * 50)  + (weightKg * 10);
    case 'PICKUP':     return 5000 + (distanceKm * 150) + (weightKg * 20);
    default:           return 3000 + (distanceKm * 100) + (weightKg * 15);
  }
}

// ===== FACTORY: Generate unique IDs =====
function generateId(prefix) {
  return prefix + '-' + Math.random().toString(36).substring(2, 10).toUpperCase();
}

// ===== SHIPMENT: Register new shipment =====
function registerShipment(origin, destination, weightKg, distanceKm, clientId) {
  const availableVehicle = DB.vehicles.find(v => v.status === 'AVAILABLE');
  const availableDriver  = DB.drivers.find(d => d.available);

  const shipment = {
    id: generateId('SHP'),
    origin, destination,
    weight: parseFloat(weightKg),
    distance: parseFloat(distanceKm),
    status: 'PENDING',
    client: clientId,
    driver: null, vehicle: null, cost: 0
  };

  if (availableVehicle && availableDriver) {
    shipment.status  = 'ASSIGNED';
    shipment.driver  = availableDriver.id;
    shipment.vehicle = availableVehicle.id;
    shipment.cost    = calculateCost(availableVehicle.type, distanceKm, weightKg);

    // Mark resources as busy
    availableVehicle.status = 'ON_TRIP';
    availableDriver.available = false;

    // Auto-generate invoice
    DB.invoices.push({ id: generateId('INV'), shipmentId: shipment.id, amount: shipment.cost, paid: false });
  }

  DB.shipments.push(shipment);
  return shipment;
}

// ===== HELPERS =====
function getClientName(id)  { const c = DB.clients.find(c => c.id === id);  return c ? c.name  : 'N/A'; }
function getDriverName(id)  { const d = DB.drivers.find(d => d.id === id);  return d ? d.name  : 'N/A'; }
function getVehiclePlate(id){ const v = DB.vehicles.find(v => v.id === id); return v ? v.plate : 'N/A'; }

function formatRWF(amount) { return amount.toLocaleString() + ' RWF'; }

function statusBadge(status) {
  const map = {
    PENDING:    'badge-pending',
    ASSIGNED:   'badge-assigned',
    IN_TRANSIT: 'badge-transit',
    DELIVERED:  'badge-delivered',
    CANCELLED:  'badge-cancelled'
  };
  return `<span class="badge ${map[status] || ''}">${status.replace('_',' ')}</span>`;
}

function showAlert(containerId, message, type = 'success') {
  const el = document.getElementById(containerId);
  if (!el) return;
  el.innerHTML = `<div class="alert alert-${type}"><span>${type === 'success' ? '✔' : '✘'}</span> ${message}</div>`;
  setTimeout(() => { el.innerHTML = ''; }, 4000);
}

// ===== SIDEBAR ACTIVE LINK =====
function setActiveNav() {
  const page = window.location.pathname.split('/').pop();
  document.querySelectorAll('.nav-link').forEach(link => {
    if (link.getAttribute('href') === page) link.classList.add('active');
  });
}

// ===== RENDER SIDEBAR USER =====
function renderSidebarUser() {
  const user = JSON.parse(sessionStorage.getItem('sltms_user') || '{}');
  const el = document.getElementById('sidebar-user-name');
  const rl = document.getElementById('sidebar-user-role');
  if (el) el.textContent = user.name || 'Admin';
  if (rl) rl.textContent = user.role || 'ADMIN';
}
