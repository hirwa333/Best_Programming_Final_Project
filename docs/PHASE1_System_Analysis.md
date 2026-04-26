# PHASE 1 — System Analysis & Design
## Smart Logistics & Transportation Management System
### Case Study: Impano Gateway Logistics Ltd

---

## i. General Description and Analysis of the Case Study

**Impano Gateway Logistics Ltd** is a Rwandan logistics and transportation company that
provides freight forwarding, cargo tracking, fleet management, and last-mile delivery
services across Rwanda and the East African region. The company handles hundreds of
shipments daily, manages a fleet of trucks and motorcycles, and coordinates with multiple
drivers, warehouses, and clients.

Currently, the company relies on manual processes — phone calls, paper-based records,
and spreadsheets — to manage shipments, assign drivers, and track deliveries. This leads
to delays, lost shipments, poor customer communication, and inefficient fleet utilization.

The proposed **Smart Logistics & Transportation Management System (SLTMS)** is a
Java-based desktop application that digitizes and automates the core logistics operations
of Impano Gateway Logistics Ltd.

---

## ii. Functional Diagram — Internal Working of Impano Gateway Logistics Ltd

```
+------------------+       +-------------------+       +------------------+
|    CLIENT        | ----> |  SHIPMENT BOOKING | ----> |  WAREHOUSE MGMT  |
| (Places Order)   |       |  (Order Created)  |       |  (Cargo Stored)  |
+------------------+       +-------------------+       +------------------+
                                                               |
                                                               v
+------------------+       +-------------------+       +------------------+
|  INVOICE/BILLING | <---- |  DELIVERY CONFIRM | <---- |  DRIVER ASSIGNED |
|  (Payment Done)  |       |  (Status Updated) |       |  (Route Planned) |
+------------------+       +-------------------+       +------------------+
                                                               |
                                                               v
                                                    +------------------+
                                                    |  FLEET MANAGEMENT|
                                                    |  (Vehicle Track) |
                                                    +------------------+
```

**Key Departments:**
- **Operations Department** — Manages shipment bookings and driver assignments
- **Fleet Department** — Maintains vehicles, tracks locations, schedules maintenance
- **Warehouse Department** — Receives, stores, and dispatches cargo
- **Finance Department** — Handles invoicing, payments, and financial reports
- **Customer Service** — Handles client communication and delivery status updates

---

## iii. Problems Faced by Impano Gateway Logistics Ltd

| # | Problem | Impact |
|---|---------|--------|
| 1 | Manual shipment tracking via phone calls | Delays, miscommunication, lost shipments |
| 2 | No centralized driver/vehicle assignment system | Inefficient fleet use, idle vehicles |
| 3 | Paper-based invoicing and billing | Errors, slow payment collection |
| 4 | No real-time delivery status for clients | Poor customer satisfaction |
| 5 | No maintenance schedule for fleet | Unexpected vehicle breakdowns |
| 6 | Difficulty generating operational reports | Poor management decisions |

---

## iv. Object-Oriented System Analysis & Design

### 1. Use Case Diagram

```
                    +------------------------------------------+
                    |   Smart Logistics & Transport System      |
                    |                                          |
  +----------+      |  [Register Shipment]                     |
  |  Client  |----->|  [Track Shipment]                        |
  +----------+      |  [View Invoice]                          |
                    |  [Request Delivery]                      |
                    |                                          |
  +----------+      |  [Assign Driver to Shipment]             |
  |  Driver  |----->|  [Update Delivery Status]                |
  +----------+      |  [View Assigned Routes]                  |
                    |                                          |
  +----------+      |  [Manage Fleet/Vehicles]                 |
  | Manager  |----->|  [Generate Reports]                      |
  +----------+      |  [Manage Clients]                        |
                    |  [Manage Drivers]                        |
  +----------+      |  [Process Invoice/Payment]               |
  |  Admin   |----->|  [Manage Users]                          |
  +----------+      |  [Configure System]                      |
                    +------------------------------------------+
```

---

### 2. Class Diagram

```
+------------------+          +---------------------+
|     User         |          |     Shipment        |
|------------------|          |---------------------|
| - userId: String |          | - shipmentId: String|
| - name: String   |          | - origin: String    |
| - email: String  |          | - destination: String|
| - role: Role     |          | - weight: double    |
|------------------|          | - status: Status    |
| + login()        |          | - client: Client    |
| + logout()       |          | - driver: Driver    |
+------------------+          | - vehicle: Vehicle  |
        ^                     |---------------------|
        |                     | + updateStatus()    |
   +---------+                | + calculateCost()   |
   |         |                +---------------------+
+--------+ +--------+                  |
| Client | | Driver |                  |
|--------| |--------|         +------------------+
| phone  | | license|         |    Invoice       |
| address| | status |         |------------------|
+--------+ +--------+         | - invoiceId      |
                              | - amount: double |
+------------------+          | - isPaid: boolean|
|    Vehicle       |          | - shipment       |
|------------------|          |------------------|
| - vehicleId      |          | + generatePDF()  |
| - plateNumber    |          | + markAsPaid()   |
| - type: VehicleType         +------------------+
| - capacity: double|
| - status: VStatus|
|------------------|
| + assignDriver() |
| + scheduleService|
+------------------+

+------------------+
|    Route         |
|------------------|
| - routeId        |
| - startPoint     |
| - endPoint       |
| - distance: double|
| - estimatedTime  |
|------------------|
| + calculateETA() |
+------------------+
```

---

### 3. Activity Diagram — Shipment Booking Process

```
[Client Requests Shipment]
          |
          v
[System Validates Client Info]
          |
     [Valid?]---NO--->[Show Error & Request Correction]
          |
         YES
          v
[Create Shipment Record]
          |
          v
[Check Available Vehicles]
          |
    [Available?]---NO--->[Notify Client of Delay]
          |
         YES
          v
[Assign Vehicle & Driver]
          |
          v
[Calculate Shipping Cost]
          |
          v
[Generate Invoice]
          |
          v
[Notify Client via System]
          |
          v
[Driver Picks Up Cargo]
          |
          v
[Update Status: IN_TRANSIT]
          |
          v
[Deliver to Destination]
          |
          v
[Update Status: DELIVERED]
          |
          v
[Mark Invoice as Paid]
          |
          v
[END]
```

---

### 4. Sequence Diagram — Shipment Registration

```
Client        UI           ShipmentService    VehicleRepo    DriverRepo    InvoiceService
  |            |                  |                |              |               |
  |--register->|                  |                |              |               |
  |            |--createShipment->|                |              |               |
  |            |                  |--findVehicle-->|              |               |
  |            |                  |<--vehicle------|              |               |
  |            |                  |--findDriver------------------->|              |
  |            |                  |<--driver----------------------|               |
  |            |                  |--assignDriverToShipment()     |               |
  |            |                  |--generateInvoice----------------------------------->|
  |            |                  |<--invoice------------------------------------------|
  |            |<--shipmentCreated|                |              |               |
  |<--confirm--|                  |                |              |               |
```

---

### 5. Component Diagram

```
+-------------------------------------------------------+
|          Smart Logistics & Transport System           |
|                                                       |
|  +--------------+      +------------------+           |
|  |   UI Layer   |----->| Service Layer    |           |
|  | (Console UI) |      |------------------|           |
|  +--------------+      | ShipmentService  |           |
|                        | DriverService    |           |
|                        | VehicleService   |           |
|                        | InvoiceService   |           |
|                        | ReportService    |           |
|                        +------------------+           |
|                               |                       |
|                        +------------------+           |
|                        | Repository Layer |           |
|                        |------------------|           |
|                        | ShipmentRepo     |           |
|                        | DriverRepo       |           |
|                        | VehicleRepo      |           |
|                        | ClientRepo       |           |
|                        +------------------+           |
|                               |                       |
|                        +------------------+           |
|                        |   Data Layer     |           |
|                        | (In-Memory Store)|           |
|                        +------------------+           |
|                                                       |
|  +------------------+  +------------------+           |
|  | Factory Pattern  |  | Strategy Pattern |           |
|  | (ShipmentFactory)|  | (CostStrategy)   |           |
|  +------------------+  +------------------+           |
+-------------------------------------------------------+
```
