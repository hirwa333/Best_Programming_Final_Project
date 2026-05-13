# 🚛 Smart Logistics & Transportation Management System (SLTMS)

> **Case Study:** Impano Gateway Logistics Ltd — Kigali, Rwanda  
> **Student:** Hirwa Roy | **ID:** 24174  
> **Course:** Best Programming Practices and Design Patterns  
> **Instructor:** RUTARINDWA JEAN PIERRE  
> **University:** AUCA — Adventist University of Central Africa  
> **Academic Year:** 2025 — 2026

---

## 📌 Project Overview

**Impano Gateway Logistics Ltd** is a Rwandan logistics and transportation company that handles freight forwarding, cargo tracking, fleet management, and last-mile delivery across Rwanda and East Africa.

Before this system, the company relied on **manual processes** — phone calls, paper records, and spreadsheets — to manage shipments, assign drivers, and track deliveries. This caused:

- Lost shipments and delivery delays
- Inefficient use of vehicles and drivers
- Billing errors and slow payment collection
- Poor customer communication

The **Smart Logistics & Transportation Management System (SLTMS)** is a **Java 17 console application** that digitizes and automates all core logistics operations of Impano Gateway Logistics Ltd.

---

## ✅ What the System Does

| Module | Description |
|--------|-------------|
| 👤 Client Management | Register and manage clients who send cargo |
| 🚗 Driver Management | Register drivers, track availability |
| 🚛 Fleet Management | Add vehicles (Truck, Van, Motorcycle), track status |
| 📦 Shipment Booking | Create shipments, auto-assign driver and vehicle |
| 💰 Cost Calculation | Auto-calculate cost based on vehicle type, weight, distance |
| 🧾 Invoice Management | Auto-generate invoice per shipment, mark as paid |
| 📊 Reports | Show total clients, shipments, revenue, pending deliveries |

---

## 🏗️ Project Structure

```
Smart_Logistics/
├── pom.xml                          ← Maven build file
├── Dockerfile                       ← Docker container definition
├── .gitignore
├── docs/                            ← All phase documentation
│   ├── PHASE1_System_Analysis.md
│   ├── PHASE2_Prototype_Design.md
│   ├── PHASE3_Docker_and_Git.md
│   └── PHASE4_Test_Plan.md
└── src/
    ├── main/java/com/impano/logistics/
    │   ├── Main.java                ← Entry point
    │   ├── model/                   ← Domain entities (User, Client, Driver, Vehicle, Shipment, Invoice)
    │   ├── strategy/                ← Strategy Pattern (cost calculation)
    │   ├── factory/                 ← Factory Pattern (object creation)
    │   ├── repository/              ← Data access layer (in-memory storage)
    │   ├── service/                 ← Business logic layer
    │   └── ui/
    │       └── ConsoleApp.java      ← Interactive console menu
    └── test/java/com/impano/logistics/
        └── AppTest.java             ← Test runner (14 test cases)
```

---

## 🎨 Design Patterns Used

### 1. Strategy Pattern (Behavioral)
Different vehicle types use different pricing formulas. Instead of if-else chains, each vehicle type has its own strategy class.

```
<<interface>>
CostCalculationStrategy
+ calculate(Shipment): double
         ^
         |
  ┌──────┼──────────┐
  │      │          │
Truck   Van    Motorcycle
Cost   Cost     Cost
```

- **Truck:** 5,000 + (distance × 150) + (weight × 20) RWF
- **Van:** 3,000 + (distance × 100) + (weight × 15) RWF
- **Motorcycle:** 1,000 + (distance × 50) + (weight × 10) RWF

### 2. Factory Pattern (Creational)
Centralizes object creation to ensure consistent initialization and unique ID generation.

```java
// Every shipment gets a unique ID automatically
Shipment s = ShipmentFactory.createShipment(origin, destination, weight, distance, client);

// Correct pricing strategy selected automatically
CostCalculationStrategy strategy = CostStrategyFactory.getStrategy(vehicle.getType());
```

---

## 🏛️ Architecture

```
┌─────────────────────────────────────────┐
│           Console UI Layer              │
│         (ConsoleApp.java)               │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│           Service Layer                 │
│  ShipmentService | ClientService        │
│  DriverService   | VehicleService       │
│  InvoiceService                         │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│          Repository Layer               │
│  ShipmentRepo | ClientRepo              │
│  DriverRepo   | VehicleRepo             │
│  InvoiceRepo                            │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│           Model Layer                   │
│  User | Client | Driver | Vehicle       │
│  Shipment | Invoice | Enums             │
└─────────────────────────────────────────┘
```

---

## 🚀 How to Run

### Prerequisites
- Java 17+
- Maven 3.6+

### Build & Run
```bash
# Clone the project
git clone https://github.com/hirwa333/Best_Programming_Final_Project.git
cd Best_Programming_Final_Project

# Build
mvn clean package

# Run
java -jar target/smart-logistics-1.0.jar
```

### Run Tests
```bash
mvn exec:java -Dexec.mainClass="com.impano.logistics.AppTest"
```

---

## 🐳 Run with Docker

```bash
# Build the Docker image
docker build -t impano-logistics:1.0 .

# Run the container (interactive mode for console app)
docker run -it impano-logistics:1.0
```

The Dockerfile uses a **two-stage build**:
- Stage 1: Maven compiles and packages the app into a JAR
- Stage 2: Lightweight JRE Alpine image runs the JAR

---

## 🧪 Testing

14 test cases covering all core modules — all passing ✅

| Test | Description | Result |
|------|-------------|--------|
| TC-001 | Client registration with valid data | ✅ PASS |
| TC-002 | Reject duplicate client email | ✅ PASS |
| TC-003 | Shipment creation with available resources | ✅ PASS |
| TC-004 | Shipment creation with no resources | ✅ PASS |
| TC-005 | Shipment status update | ✅ PASS |
| TC-006 | Driver/vehicle freed after delivery | ✅ PASS |
| TC-007 | Truck cost calculation | ✅ PASS |
| TC-008 | Van cost calculation | ✅ PASS |
| TC-009 | Motorcycle cost calculation | ✅ PASS |
| TC-010 | Invoice auto-generation | ✅ PASS |
| TC-011 | Pay invoice | ✅ PASS |
| TC-012 | Invalid shipment ID lookup | ✅ PASS |
| TC-013 | Factory — unique shipment IDs | ✅ PASS |
| TC-014 | Strategy factory — correct strategy selection | ✅ PASS |

---

## 📋 Project Phases

| Phase | Description |
|-------|-------------|
| **Phase 1** | System analysis, UML diagrams (Use Case, Class, Activity, Sequence, Component) |
| **Phase 2** | Java prototype with Strategy & Factory patterns, Google Java Style Guide |
| **Phase 3** | Dockerized application + Git version control |
| **Phase 4** | Software test plan with 14 test cases |

---

## 🛠️ Technologies Used

| Technology | Purpose |
|------------|---------|
| Java 17 | Main programming language |
| Maven 3.9 | Build and dependency management |
| Docker | Application containerization |
| Git | Version control |
| GitHub | Remote repository hosting |

---

## 📞 Contact

**Hirwa Roy** — Student ID: 24174  
AUCA — Faculty of Information Technology  
Department of Software Engineering
