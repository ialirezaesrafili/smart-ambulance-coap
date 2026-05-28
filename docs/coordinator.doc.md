# Coordinator

# DriverCoordinator.java – Driver Orchestrator

## Overview

`DriverCoordinator` is the high‑level orchestrator for the **driver (D)** role in the smart ambulance simulation.  
It manages four `DriverClient` instances – one for each relevant resource (cockpit AC, rear cabin AC, cockpit HMI, rear
cabin HMI) – and provides methods that **simultaneously** perform the required operations on both cabins, as mandated by
the project specification.

**Location:** `com.ambulance.coordinator.DriverCoordinator`

---

## Responsibilities

- Instantiate and hold all `DriverClient` objects needed by the driver.
- Provide a single, simple interface to set the temperature in **both** cabins at once.
- Provide a method to update emergency information on **both** HMIs in a logically simultaneous manner.
- Read current temperatures for verification.
- Format and display the JSON responses using `JsonCliRenderer` for terminal readability.

---

## Fields

| Field          | Type           | Description                                                                       |
|----------------|----------------|-----------------------------------------------------------------------------------|
| `cockpitAc`    | `DriverClient` | Client targeting the cockpit air‑conditioning resource (`/ac` on port `5683`).    |
| `rearCabinAc`  | `DriverClient` | Client targeting the rear cabin air‑conditioning resource (`/ac` on port `5684`). |
| `cockpitHmi`   | `DriverClient` | Client targeting the cockpit HMI resource (`/hmi` on port `5683`).                |
| `rearCabinHmi` | `DriverClient` | Client targeting the rear cabin HMI resource (`/hmi` on port `5684`).             |

All fields are `private final` – they are initialised once in the constructor and never changed.

---

## Constructor

### `DriverCoordinator(String cockpitHost, int cockpitPort, String rearCabinHost, int rearCabinPort)`

```java
public DriverCoordinator(String cockpitHost, int cockpitPort,
                         String rearCabinHost, int rearCabinPort);
```

---

# GpsCoordinator.java – GPS Orchestrator

## Overview

`GpsCoordinator` is the high‑level orchestrator for the **GPS receiver (G)** role in the smart ambulance simulation.  
It manages two `GpsClient` instances – one for each HMI (cockpit and rear cabin) – and provides a method that *
*simultaneously** updates the GPS coordinates on both HMIs, as required by the project specification.

**Location:** `com.ambulance.coordinator.GpsCoordinator`

---

## Responsibilities

- Instantiate and hold all `GpsClient` objects needed by the GPS subsystem.
- Provide a single, simple interface to push a new GPS coordinate string to **both** HMIs at once.
- Format and display the JSON responses using `JsonCliRenderer` for terminal readability.

---

## Fields

| Field          | Type        | Description                                                           |
|----------------|-------------|-----------------------------------------------------------------------|
| `cockpitHmi`   | `GpsClient` | Client targeting the cockpit HMI resource (`/hmi` on port `5683`).    |
| `rearCabinHmi` | `GpsClient` | Client targeting the rear cabin HMI resource (`/hmi` on port `5684`). |

Both fields are `private final` – they are initialised once in the constructor and never changed.

---

## Constructor

### `GpsCoordinator(String cockpitHost, int cockpitPort, String rearCabinHost, int rearCabinPort)`

```java
public GpsCoordinator(String cockpitHost, int cockpitPort,
                      String rearCabinHost, int rearCabinPort);
```

---

# RelieverCoordinator.java – Reliever Orchestrator

## Overview

`RelieverCoordinator` is the high‑level orchestrator for the **reliever (R)** role in the smart ambulance simulation.  
It manages three `RelieverClient` instances – one for the rear cabin air‑conditioning, and one for each HMI (cockpit and
rear cabin). It provides methods to set the temperature only in the rear cabin, update emergency information on both
HMIs simultaneously, and deliberately test the forbidden action of setting the cockpit temperature to demonstrate
server‑side authorisation.

**Location:** `com.ambulance.coordinator.RelieverCoordinator`

---

## Responsibilities

- Instantiate and hold all `RelieverClient` objects needed by the reliever.
- Provide a method to set the temperature in the **rear cabin only** (authorised action).
- Provide a method to update emergency information on **both** HMIs in a logically simultaneous manner.
- Include a demonstration method that intentionally attempts to set the cockpit temperature, which the server must
  reject with `4.03 Forbidden`.
- Format and display JSON responses using `JsonCliRenderer`.

---

## Fields

| Field          | Type             | Description                                                                       |
|----------------|------------------|-----------------------------------------------------------------------------------|
| `rearCabinAc`  | `RelieverClient` | Client targeting the rear cabin air‑conditioning resource (`/ac` on port `5684`). |
| `cockpitHmi`   | `RelieverClient` | Client targeting the cockpit HMI resource (`/hmi` on port `5683`).                |
| `rearCabinHmi` | `RelieverClient` | Client targeting the rear cabin HMI resource (`/hmi` on port `5684`).             |

All fields are `private final`. No field for cockpit AC is stored persistently because the reliever is not allowed to
change it; a one‑off client is created only for testing the forbidden case.

---

## Constructor

### `RelieverCoordinator(String cockpitHost, int cockpitPort, String rearCabinHost, int rearCabinPort)`

```java
public RelieverCoordinator(String cockpitHost, int cockpitPort,
                           String rearCabinHost, int rearCabinPort);
```

---
