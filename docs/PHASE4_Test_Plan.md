# PHASE 4 — Software Test Plan
## Smart Logistics & Transportation Management System
### Impano Gateway Logistics Ltd

---

## 1. Introduction

### 1.1 Purpose
This Software Test Plan (STP) defines the testing strategy, scope, objectives, resources,
and schedule for testing the Smart Logistics & Transportation Management System (SLTMS)
developed for Impano Gateway Logistics Ltd.

### 1.2 Scope
This plan covers testing of all functional modules:
- Client Management
- Driver Management
- Vehicle Management
- Shipment Management (including auto-assignment)
- Invoice Management
- Cost Calculation (Strategy Pattern)
- Authentication (Login / Logout)
- Report Generation

### 1.3 Testing Goals — Aligned with System Requirements

| Goal | System Requirement It Covers |
|------|------------------------------|
| Verify client registration works correctly | REQ-01: System must register clients |
| Verify duplicate emails are rejected | REQ-01: Each client must have a unique email |
| Verify shipment auto-assignment | REQ-02: System must auto-assign driver and vehicle |
| Verify cost calculation per vehicle type | REQ-03: Cost must be calculated based on vehicle type |
| Verify invoice is auto-generated | REQ-04: Invoice must be created for every shipment |
| Verify login/logout works | REQ-05: Only authenticated users can access the system |
| Verify status transitions are correct | REQ-06: Shipment must follow PENDING→ASSIGNED→IN_TRANSIT→DELIVERED |
| Verify resources freed after delivery | REQ-07: Driver and vehicle must be available after delivery |

---

## 2. Test Items

| Module | Class Under Test |
|--------|-----------------|
| Authentication | AuthService |
| Client Management | ClientService |
| Driver Management | DriverService |
| Vehicle Management | VehicleService |
| Shipment Management | ShipmentService |
| Invoice Management | InvoiceService |
| Cost Calculation | TruckCostStrategy, VanCostStrategy, MotorcycleCostStrategy |
| Object Creation | ShipmentFactory, CostStrategyFactory |

---

## 3. Features to Be Tested

| # | Feature | Test Type |
|---|---------|-----------|
| 1 | Login with valid credentials | Functional |
| 2 | Login with invalid credentials | Negative |
| 3 | Logout clears session | Functional |
| 4 | Client registration with valid data | Functional |
| 5 | Reject duplicate client email | Negative/Validation |
| 6 | Shipment creation with available driver & vehicle | Functional |
| 7 | Shipment creation with no available resources | Negative |
| 8 | Automatic driver & vehicle assignment | Functional |
| 9 | Shipment status update | Functional |
| 10 | Driver/vehicle freed after delivery | Functional |
| 11 | Invoice auto-generated on shipment creation | Functional |
| 12 | Invoice payment marking | Functional |
| 13 | Truck cost calculation formula | Unit |
| 14 | Van cost calculation formula | Unit |
| 15 | Motorcycle cost calculation formula | Unit |
| 16 | Report generation totals | Functional |
| 17 | Invalid shipment ID lookup | Negative |
| 18 | Factory generates unique shipment IDs | Unit |

---

## 4. Test Cases

### TC-001: Login — Valid Credentials
- **Input:** email="admin@impano.rw", password="admin123"
- **Expected:** Login successful, currentUser set, isLoggedIn() = true
- **Result:** ✅ PASS

### TC-002: Login — Invalid Credentials
- **Input:** email="admin@impano.rw", password="wrongpass"
- **Expected:** Login fails, Optional.empty() returned, isLoggedIn() = false
- **Result:** ✅ PASS

### TC-003: Logout
- **Input:** User logged in, calls logout()
- **Expected:** currentUser = null, isLoggedIn() = false
- **Result:** ✅ PASS

### TC-004: Client Registration — Valid Data
- **Input:** name="Alice Uwimana", email="alice@impano.rw", phone="+250788001001", address="Kigali"
- **Expected:** Client created with ID starting "CLT-", all fields stored correctly
- **Result:** ✅ PASS

### TC-005: Client Registration — Duplicate Email
- **Input:** Register two clients with same email "bob@impano.rw"
- **Expected:** Second registration throws IllegalArgumentException
- **Result:** ✅ PASS

### TC-006: Shipment Creation — Resources Available
- **Input:** Client registered, 1 driver available, 1 vehicle available, origin="Kigali", destination="Butare", weight=200kg, distance=130km
- **Expected:** Shipment status=ASSIGNED, driver assigned, vehicle assigned, cost>0, invoice generated
- **Result:** ✅ PASS

### TC-007: Shipment Creation — No Resources
- **Input:** No drivers or vehicles in system
- **Expected:** Shipment status=PENDING, no driver/vehicle assigned, cost=0
- **Result:** ✅ PASS

### TC-008: Shipment Status Update
- **Input:** Valid shipment ID, new status=IN_TRANSIT
- **Expected:** updateStatus() returns true, shipment.getStatus()==IN_TRANSIT
- **Result:** ✅ PASS

### TC-009: Driver/Vehicle Released After Delivery
- **Input:** Shipment in ASSIGNED state, update to DELIVERED
- **Expected:** driver.isAvailable()==true, vehicle.getStatus()==AVAILABLE
- **Result:** ✅ PASS

### TC-010: Truck Cost Calculation
- **Formula:** 5000 + (distance × 150) + (weight × 20)
- **Input:** distance=100km, weight=200kg
- **Expected:** 5000 + 15000 + 4000 = 24,000 RWF
- **Result:** ✅ PASS

### TC-011: Van Cost Calculation
- **Formula:** 3000 + (distance × 100) + (weight × 15)
- **Input:** distance=50km, weight=100kg
- **Expected:** 3000 + 5000 + 1500 = 9,500 RWF
- **Result:** ✅ PASS

### TC-012: Motorcycle Cost Calculation
- **Formula:** 1000 + (distance × 50) + (weight × 10)
- **Input:** distance=20km, weight=10kg
- **Expected:** 1000 + 1000 + 100 = 2,100 RWF
- **Result:** ✅ PASS

### TC-013: Invoice Auto-Generation
- **Input:** Shipment created with available resources
- **Expected:** Invoice found by shipment ID, isPaid()==false, amount==shipment cost
- **Result:** ✅ PASS

### TC-014: Pay Invoice
- **Input:** Valid invoice ID
- **Expected:** payInvoice() returns true, invoice.isPaid()==true
- **Result:** ✅ PASS

### TC-015: Invalid Shipment ID Lookup
- **Input:** shipmentId="SHP-INVALID"
- **Expected:** findById() returns Optional.empty()
- **Result:** ✅ PASS

### TC-016: Factory — Unique Shipment IDs
- **Input:** Create 100 shipments
- **Expected:** All shipment IDs are unique, all start with "SHP-"
- **Result:** ✅ PASS

---

## 5. Test Environment

| Item | Details |
|------|---------|
| Operating System | Windows 11 |
| JDK | Java 17 (Eclipse Temurin) |
| Build Tool | Maven 3.9.6 |
| Test Runner | Manual — AppTest.java |
| Docker | Docker Desktop 4.x |
| Version Control | Git + GitHub |

---

## 6. Test Schedule

| Week | Activity | Duration |
|------|----------|----------|
| Week 1 | Unit testing of models, services, strategies | 2 days |
| Week 1 | Integration testing of service + repository | 1 day |
| Week 2 | End-to-end testing via ConsoleApp (login → dashboard → logout) | 2 days |
| Week 2 | Negative/boundary testing | 1 day |
| Week 2 | Docker container testing | 1 day |

---

## 7. Entry and Exit Criteria

### Entry Criteria
- All source code compiled without errors (mvn clean package)
- All repositories and services implemented
- Seed data (drivers, vehicles, admin user) available
- Docker image builds successfully

### Exit Criteria
- All 16 test cases executed
- 100% of critical test cases pass
- No unresolved Critical or High severity defects
- Application runs successfully inside Docker container

---

## 8. Tools and Methods for Tracking Issues

### Primary Tool: GitHub Issues
All defects and issues found during testing are tracked using **GitHub Issues** at:
**https://github.com/hirwa333/Best_Programming_Final_Project/issues**

**How to report a bug on GitHub Issues:**
1. Go to the repository on GitHub
2. Click **Issues** → **New Issue**
3. Fill in the title, description, steps to reproduce, and expected vs actual result
4. Assign a label: `bug`, `enhancement`, or `question`
5. Assign to the developer responsible
6. Track progress: Open → In Progress → Closed

### Issue Labels Used

| Label | Color | Meaning |
|-------|-------|---------|
| `bug` | Red | Something is not working correctly |
| `critical` | Dark Red | System crash or data loss |
| `enhancement` | Blue | Feature improvement request |
| `test-fail` | Orange | A test case failed |
| `fixed` | Green | Issue has been resolved |

### Defect Severity Levels

| Severity | Description | Resolution Time | Example |
|----------|-------------|-----------------|---------|
| Critical | System crash, data loss | Same day | App throws NullPointerException on startup |
| High | Feature completely broken | Within 24 hours | Shipment creation fails for all inputs |
| Medium | Feature partially broken | Within 48 hours | Cost calculation gives wrong result |
| Low | Minor UI/display issue | Next sprint | Menu alignment is off |

### Secondary Tool: Console Test Runner (AppTest.java)
The AppTest.java class runs all test cases automatically and prints PASS/FAIL results to the console. Run with:
```bash
mvn exec:java -Dexec.mainClass="com.impano.logistics.AppTest"
```

---

## 9. Risks and Mitigations

| Risk | Mitigation |
|------|-----------|
| In-memory data lost on restart | Acceptable for prototype; file persistence planned for v2 |
| No concurrent access testing | Single-user console app; not applicable for prototype |
| Manual test runner (no JUnit) | AppTest.java covers all critical paths; JUnit planned for v2 |
