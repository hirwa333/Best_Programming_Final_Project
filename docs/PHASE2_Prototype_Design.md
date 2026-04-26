# PHASE 2 — Software Development Prototype & Design Patterns
## Smart Logistics & Transportation Management System
### Impano Gateway Logistics Ltd

---

## 1. Prototype Overview

The SLTMS prototype is a Java 17 console application built using Maven. It implements
the core logistics operations of Impano Gateway Logistics Ltd:

- Client registration and management
- Driver registration and management
- Fleet (vehicle) management
- Shipment booking with automatic resource assignment
- Cost calculation based on vehicle type
- Invoice generation and payment
- Operational report generation

### Programming Best Practices Applied (Google Java Style Guide)

| Practice | Implementation |
|----------|---------------|
| Naming conventions | camelCase for methods/variables, PascalCase for classes |
| Package structure | com.impano.logistics.{model,service,repository,strategy,factory,ui} |
| Single Responsibility | Each class has one clear responsibility |
| Encapsulation | All fields private, accessed via getters/setters |
| Immutability | IDs set once in constructor, never changed |
| Fail-fast validation | IllegalArgumentException thrown immediately on bad input |
| Optional usage | Repository methods return Optional<T> instead of null |
| Meaningful names | registerShipment(), findAvailable(), markAsPaid() |
| No magic numbers | Pricing constants documented in strategy classes |

---

## 2. Design Patterns Used

### Pattern 1: Strategy Pattern (Behavioral)

**Location:** `com.impano.logistics.strategy`

**Problem:** Different vehicle types (Truck, Van, Motorcycle) have different pricing
formulas. Using if-else chains in one class violates the Open/Closed Principle.

**Solution:** Define a CostCalculationStrategy interface. Each vehicle type implements
its own pricing algorithm. The correct strategy is selected at runtime.

```
<<interface>>
CostCalculationStrategy
+ calculate(Shipment): double
        ^
        |
   +---------+-----------+
   |         |           |
TruckCost  VanCost  MotorcycleCost
Strategy   Strategy   Strategy
```

**How it's used in ShipmentService:**
```java
CostCalculationStrategy strategy = CostStrategyFactory.getStrategy(vehicle.getType());
double cost = strategy.calculate(shipment);
```

**Benefit:** Adding a new vehicle type (e.g., BOAT) only requires creating a new
BoatCostStrategy class — no existing code changes needed.

---

### Pattern 2: Factory Pattern (Creational)

**Location:** `com.impano.logistics.factory`

**Problem:** Creating Shipment objects requires generating unique IDs and ensuring
consistent initialization. Scattering this logic across the codebase leads to
duplication and inconsistency.

**Solution:** ShipmentFactory centralizes all Shipment creation. CostStrategyFactory
centralizes strategy selection based on vehicle type.

```java
// ShipmentFactory — ensures every shipment gets a unique ID
public static Shipment createShipment(String origin, String destination,
                                      double weightKg, double distanceKm, Client client) {
    String id = "SHP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    return new Shipment(id, origin, destination, weightKg, distanceKm, client);
}

// CostStrategyFactory — returns correct strategy for vehicle type
public static CostCalculationStrategy getStrategy(VehicleType type) {
    switch (type) {
        case TRUCK, PICKUP -> return new TruckCostStrategy();
        case VAN            -> return new VanCostStrategy();
        case MOTORCYCLE     -> return new MotorcycleCostStrategy();
        default             -> return new VanCostStrategy();
    }
}
```

**Benefit:** Object creation is centralized, consistent, and easy to modify.

---

## 3. Project Structure

```
Smart_Logistics/
├── pom.xml                          (Maven build file)
├── Dockerfile                       (Docker container definition)
├── .gitignore
├── docs/
│   ├── PHASE1_System_Analysis.md
│   ├── PHASE2_Prototype_Design.md
│   ├── PHASE3_Docker_and_Git.md
│   └── PHASE4_Test_Plan.md
└── src/
    ├── main/java/com/impano/logistics/
    │   ├── Main.java                (Entry point)
    │   ├── model/                   (Domain entities)
    │   │   ├── User.java
    │   │   ├── Client.java
    │   │   ├── Driver.java
    │   │   ├── Vehicle.java
    │   │   ├── Shipment.java
    │   │   ├── Invoice.java
    │   │   ├── Role.java
    │   │   ├── ShipmentStatus.java
    │   │   ├── VehicleType.java
    │   │   └── VehicleStatus.java
    │   ├── strategy/                (Strategy Pattern)
    │   │   ├── CostCalculationStrategy.java
    │   │   ├── TruckCostStrategy.java
    │   │   ├── VanCostStrategy.java
    │   │   └── MotorcycleCostStrategy.java
    │   ├── factory/                 (Factory Pattern)
    │   │   ├── ShipmentFactory.java
    │   │   └── CostStrategyFactory.java
    │   ├── repository/              (Data access layer)
    │   │   ├── ClientRepository.java
    │   │   ├── DriverRepository.java
    │   │   ├── VehicleRepository.java
    │   │   ├── ShipmentRepository.java
    │   │   └── InvoiceRepository.java
    │   ├── service/                 (Business logic layer)
    │   │   ├── ClientService.java
    │   │   ├── DriverService.java
    │   │   ├── VehicleService.java
    │   │   ├── ShipmentService.java
    │   │   └── InvoiceService.java
    │   └── ui/
    │       └── ConsoleApp.java      (User interface)
    └── test/java/com/impano/logistics/
        └── AppTest.java             (Test runner)
```

---

## 4. How to Build and Run

### Prerequisites
- Java 17+
- Maven 3.6+

### Build
```bash
cd Smart_Logistics
mvn clean package
```

### Run
```bash
java -jar target/smart-logistics-1.0.jar
```

### Run Tests
```bash
mvn exec:java -Dexec.mainClass="com.impano.logistics.AppTest"
```

### Run with Docker
```bash
docker build -t impano-logistics:1.0 .
docker run -it impano-logistics:1.0
```
