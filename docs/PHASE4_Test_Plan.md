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
- Shipment Management
- Invoice Management
- Cost Calculation (Strategy Pattern)
- Report Generation

### 1.3 Objectives
- Verify that all functional requirements are correctly implemented
- Ensure the system handles invalid inputs gracefully
- Validate that design patterns (Strategy, Factory) work correctly
- Confirm that business rules (cost calculation, status transitions) are accurate

---

## 2. Test Items

| Module | Description |
|--------|-------------|
| ClientService | Register, retrieve, and validate clients |
| DriverService | Register and retrieve drivers |
| VehicleService | Add and retrieve vehicles |
| ShipmentService | Create shipments, assign resources, update status |
| InvoiceService | Generate, retrieve, and pay invoices |
| CostCalculationStrategy | Verify pricing per vehicle type |
| ShipmentFactory | Verify unique ID generation |
| CostStrategyFactory | Verify correct strategy selection |

---

## 3. Features to Be Tested

| # | Feature | Test Type |
|---|---------|-----------|
| 1 | Client registration with valid data | Functional |
| 2 | Reject duplicate client email | Negative/Validation |
| 3 | Shipment creation with available driver & vehicle | Functional |
| 4 | Shipment creation with no available resources | Negative |
| 5 | Automatic driver & vehicle assignment | Functional |
| 6 | Shipment status update (PENDING → IN_TRANSIT → DELIVERED) | Functional |
| 7 | Driver/vehicle freed after delivery | Functional |
| 8 | Invoice auto-generated on shipment creation | Functional |
| 9 | Invoice payment marking | Functional |
| 10 | Truck cost calculation formula | Unit |
| 11 | Van cost calculation formula | Unit |
| 12 | Motorcycle cost calculation formula | Unit |
| 13 | Report generation totals | Functional |
| 14 | Invalid shipment ID lookup | Negative |

---

## 4. Test Cases

### TC-001: Client Registration — Valid Data
- **Input:** name="Alice Uwimana", email="alice@impano.rw", phone="+250788001001", address="Kigali"
- **Expected:** Client object created with ID starting "CLT-", all fields stored correctly
- **Result:** PASS ✓

### TC-002: Client Registration — Duplicate Email
- **Input:** Register two clients with same email "bob@impano.rw"
- **Expected:** Second registration throws IllegalArgumentException
- **Result:** PASS ✓

### TC-003: Shipment Creation — Resources Available
- **Input:** Client registered, 1 driver available, 1 vehicle available, origin="Kigali", destination="Butare", weight=200kg, distance=130km
- **Expected:** Shipment status = ASSIGNED, driver assigned, vehicle assigned, cost > 0, invoice generated
- **Result:** PASS ✓

### TC-004: Shipment Creation — No Resources
- **Input:** No drivers or vehicles in system
- **Expected:** Shipment status = PENDING, no driver/vehicle assigned, cost = 0
- **Result:** PASS ✓

### TC-005: Shipment Status Update
- **Input:** Valid shipment ID, new status = IN_TRANSIT
- **Expected:** updateStatus() returns true, shipment.getStatus() == IN_TRANSIT
- **Result:** PASS ✓

### TC-006: Driver/Vehicle Released After Delivery
- **Input:** Shipment in ASSIGNED state, update to DELIVERED
- **Expected:** driver.isAvailable() == true, vehicle.getStatus() == AVAILABLE
- **Result:** PASS ✓

### TC-007: Truck Cost Calculation
- **Formula:** 5000 + (distance × 150) + (weight × 20)
- **Input:** distance=100km, weight=200kg
- **Expected:** 5000 + 15000 + 4000 = 24,000 RWF
- **Result:** PASS ✓

### TC-008: Van Cost Calculation
- **Formula:** 3000 + (distance × 100) + (weight × 15)
- **Input:** distance=50km, weight=100kg
- **Expected:** 3000 + 5000 + 1500 = 9,500 RWF
- **Result:** PASS ✓

### TC-009: Motorcycle Cost Calculation
- **Formula:** 1000 + (distance × 50) + (weight × 10)
- **Input:** distance=20km, weight=10kg
- **Expected:** 1000 + 1000 + 100 = 2,100 RWF
- **Result:** PASS ✓

### TC-010: Invoice Auto-Generation
- **Input:** Shipment created with available resources
- **Expected:** Invoice found by shipment ID, isPaid() == false, amount == shipment cost
- **Result:** PASS ✓

### TC-011: Pay Invoice
- **Input:** Valid invoice ID
- **Expected:** payInvoice() returns true, invoice.isPaid() == true
- **Result:** PASS ✓

### TC-012: Invalid Shipment ID Lookup
- **Input:** shipmentId = "SHP-INVALID"
- **Expected:** findById() returns Optional.empty()
- **Result:** PASS ✓

### TC-013: Factory — Unique Shipment IDs
- **Input:** Create 100 shipments
- **Expected:** All shipment IDs are unique
- **Result:** PASS ✓

### TC-014: Strategy Factory — Correct Strategy Selection
- **Input:** VehicleType.TRUCK → TruckCostStrategy, VehicleType.MOTORCYCLE → MotorcycleCostStrategy
- **Expected:** Correct strategy instance returned for each type
- **Result:** PASS ✓

---

## 5. Test Environment

| Item | Details |
|------|---------|
| OS | Windows 11 / Ubuntu 22.04 |
| JDK | Java 17 (Eclipse Temurin) |
| Build Tool | Maven 3.9.6 |
| Test Runner | Manual (AppTest.java) |
| Docker | Docker Desktop 4.x |

---

## 6. Test Schedule

| Phase | Activity | Duration |
|-------|----------|----------|
| Week 1 | Unit testing of models and services | 2 days |
| Week 1 | Integration testing of service + repository | 1 day |
| Week 2 | End-to-end testing via ConsoleApp | 2 days |
| Week 2 | Negative/boundary testing | 1 day |
| Week 2 | Docker container testing | 1 day |

---

## 7. Entry and Exit Criteria

### Entry Criteria
- All source code compiled without errors
- All repositories and services implemented
- Test data (seed drivers and vehicles) available

### Exit Criteria
- All 14 test cases executed
- 100% of critical test cases (TC-001 to TC-012) pass
- No unresolved critical or high-severity defects

---

## 8. Defect Management

| Severity | Description | Resolution Time |
|----------|-------------|-----------------|
| Critical | System crash, data loss | Immediate (same day) |
| High | Feature not working | Within 24 hours |
| Medium | Incorrect output | Within 48 hours |
| Low | UI/display issue | Next sprint |

---

## 9. Risks and Mitigations

| Risk | Mitigation |
|------|-----------|
| In-memory data lost on restart | Acceptable for prototype; add file persistence in v2 |
| No concurrent access testing | Single-user console app; not applicable for prototype |
| No authentication system | Planned for v2 with login module |
