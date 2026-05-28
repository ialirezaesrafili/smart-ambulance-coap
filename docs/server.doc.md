# Server

# BaseAmbulanceServer.java – Abstract CoAP Server Foundation

## Overview

`BaseAmbulanceServer` is the abstract base class for both cabin CoAP servers in the smart ambulance system. It
encapsulates the Californium `CoapServer` instance, manages the server lifecycle with a thread‑safe state machine, and
defines a template method (`registerResources()`) that concrete subclasses must implement to add their specific CoAP
resources.

**Location:** `com.ambulance.server.BaseAmbulanceServer`

---

## Responsibilities

- Create and configure a Californium `CoapServer` bound to a specific port.
- Manage the server lifecycle from `CREATED` → `STARTING` → `RUNNING` → `STOPPING` → `STOPPED` (and `ERROR`).
- Provide a synchronised `start()` and `stop()` that enforce valid state transitions.
- Declare the abstract `registerResources()` method, ensuring each cabin server adds the mandatory three resources (
  Temperature, AC, HMI).
- Expose cabin type, port, and address for informational and debugging purposes.
- Log all state transitions and lifecycle events using SLF4J.

---

## Fields

| Field        | Type                           | Visibility             | Description                                                                |
|--------------|--------------------------------|------------------------|----------------------------------------------------------------------------|
| `LOG`        | `Logger`                       | `private static final` | SLF4J logger for all server instances.                                     |
| `cabinType`  | `CabinType`                    | `protected final`      | The cabin this server represents (COCKPIT or REAR_CABIN).                  |
| `coapServer` | `CoapServer`                   | `protected final`      | The underlying Californium CoAP server instance.                           |
| `port`       | `int`                          | `protected final`      | The UDP port the server binds to.                                          |
| `state`      | `AtomicReference<ServerState>` | `private final`        | Thread‑safe holder for the current server state, initialised to `CREATED`. |

---

## Constructor

### `BaseAmbulanceServer(CabinType cabinType, int port)`

```java
protected BaseAmbulanceServer(CabinType cabinType, int port);
```

# CockpitServer.java – Cockpit CoAP Server (AC)

## Overview

`CockpitServer` is the concrete CoAP server representing the **cockpit (AC)** environment of the smart ambulance.  
It extends `BaseAmbulanceServer` and registers the three mandatory resources on the cockpit cabin: temperature sensor (
`/temp`), air‑conditioning controller (`/ac`), and human‑machine interface (`/hmi`). It binds to a configurable CoAP
port, with a default of `5683`.

**Location:** `com.ambulance.server.CockpitServer`

---

## Responsibilities

- Extend `BaseAmbulanceServer` with the cockpit cabin type.
- Implement `registerResources()` to instantiate and register the three CoAP resources for the cockpit.
- Provide a convenient default port constant and a no‑arg constructor.
- Ensure the AC resource is linked to the temperature sensor so that setpoint changes immediately affect the sensor
  reading.

---

## Fields

| Field          | Type  | Visibility            | Description                                        |
|----------------|-------|-----------------------|----------------------------------------------------|
| `DEFAULT_PORT` | `int` | `public static final` | Default CoAP port for the cockpit server (`5683`). |

(No instance fields – all server infrastructure is provided by the parent.)

---

## Constructors

### `CockpitServer(int port)`

```java
public CockpitServer(int port);
```

# RearCabinServer.java – Rear Cabin CoAP Server (AR)

## Overview

`RearCabinServer` is the concrete CoAP server representing the **rear cabin (AR)** environment of the smart ambulance.  
It extends `BaseAmbulanceServer` and registers the three mandatory resources on the rear cabin: temperature sensor (
`/temp`), air‑conditioning controller (`/ac`), and human‑machine interface (`/hmi`). It binds to a configurable CoAP
port, with a default of `5684`.

**Location:** `com.ambulance.server.RearCabinServer`

---

## Responsibilities

- Extend `BaseAmbulanceServer` with the rear cabin cabin type.
- Implement `registerResources()` to instantiate and register the three CoAP resources for the rear cabin.
- Provide a convenient default port constant and a no‑arg constructor.
- Ensure the AC resource is linked to the temperature sensor so that setpoint changes immediately affect the sensor
  reading.
- Enforce the access rule that the reliever can only set the temperature in this cabin (the permission check is
  performed by the `AirConditioningResource` using `CabinType.REAR_CABIN`).

---

## Fields

| Field          | Type  | Visibility            | Description                                           |
|----------------|-------|-----------------------|-------------------------------------------------------|
| `DEFAULT_PORT` | `int` | `public static final` | Default CoAP port for the rear cabin server (`5684`). |

No instance fields – all server infrastructure is inherited.

---

## Constructors

### `RearCabinServer(int port)`

```java
public RearCabinServer(int port);
```
