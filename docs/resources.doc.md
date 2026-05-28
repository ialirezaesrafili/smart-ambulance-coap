# Resources

# AirConditioningResource.java – Air‑Conditioning Controller (SA)

## Overview

`AirConditioningResource` is the CoAP resource representing the **air‑conditioning controller (SA)** inside one cabin of
the smart ambulance.  
It implements the `CoapResource` from Californium and handles `GET` requests (to read the current target temperature)
and `PUT` requests (to set a new target). Access control is enforced via the `CabinType` enum: the driver can modify
both cabins, the reliever only the rear cabin, and the GPS client is forbidden.

**Location:** `com.ambulance.resource.AirConditioningResource`

---

## Responsibilities

- Store the target temperature set by an operator.
- Serve `GET` requests with the current target temperature in JSON.
- Accept `PUT` requests with a new target and a role identifier.
- Validate the role against the cabin’s permission rules.
- Update the linked `TemperatureSensorResource` to simulate the immediate effect of the new setpoint.
- Return appropriate CoAP response codes (`2.04 Changed`, `4.03 Forbidden`, `4.00 Bad Request`).

---

## Fields

| Field               | Type                        | Visibility             | Description                                                                          |
|---------------------|-----------------------------|------------------------|--------------------------------------------------------------------------------------|
| `LOG`               | `Logger`                    | `private static final` | SLF4J logger for the class.                                                          |
| `targetTemperature` | `double`                    | `private`              | Current target temperature (default `22.0`).                                         |
| `cabinType`         | `CabinType`                 | `private final`        | The cabin this resource belongs to (COCKPIT or REAR_CABIN).                          |
| `temperatureSensor` | `TemperatureSensorResource` | `private final`        | The temperature sensor resource linked to the same cabin, used to propagate changes. |

---

## Constructor

### `AirConditioningResource(String name, CabinType cabinType, TemperatureSensorResource temperatureSensor)`

```java
public AirConditioningResource(String name, CabinType cabinType,
                               TemperatureSensorResource temperatureSensor);
```

---

# HmiResource.java – Human‑Machine Interface (SH)

## Overview

`HmiResource` is the CoAP resource representing the **Human‑Machine Interface (SH)** inside one cabin of the smart
ambulance.  
It stores three pieces of information:

- **Emergency level** (Low / Medium / High)
- **Event location** (Street / Workplace / Public Space / Home)
- **Current GPS coordinates** (latitude,longitude)

The resource supports `GET` (to retrieve the full state) and `PUT` (to update specific fields depending on the client’s
role).  
Driver and Reliever can update emergency level and event location; the GPS client can only update the coordinates. Any
other role is rejected.

**Location:** `com.ambulance.resource.HmiResource`

---

## Responsibilities

- Maintain the in‑memory state of the HMI for a specific cabin.
- Respond to `GET` requests with a JSON representation of the full HMI state.
- Accept `PUT` requests from authorised roles and update only the permitted fields.
- Enforce authorisation: `"driver"` / `"reliever"` → update emergency info; `"gps"` → update GPS; others →
  `4.03 Forbidden`.
- Return appropriate CoAP response codes and descriptive JSON messages.

---

## Fields

| Field            | Type             | Visibility             | Description                                                                |
|------------------|------------------|------------------------|----------------------------------------------------------------------------|
| `LOG`            | `Logger`         | `private static final` | SLF4J logger for this class.                                               |
| `emergencyLevel` | `EmergencyLevel` | `private`              | Current emergency severity (default: `LOW`).                               |
| `eventLocation`  | `EventLocation`  | `private`              | Current event location type (default: `STREET`).                           |
| `gpsCoordinates` | `String`         | `private`              | Current GPS position in `"lat,lon"` format (default: `"44.8015,10.3282"`). |
| `cabinType`      | `CabinType`      | `private final`        | The cabin where this HMI is installed (COCKPIT or REAR_CABIN).             |

---

## Constructor

### `HmiResource(String name, CabinType cabinType)`

```java
public HmiResource(String name, CabinType cabinType);
```

# TemperatureSensorResource.java – Temperature Sensor (ST)

## Overview

`TemperatureSensorResource` is the CoAP resource representing the **temperature sensor (ST)** inside one cabin of the
smart ambulance.  
It accepts only `GET` requests, returning the current cabin temperature as a JSON object. The temperature value can be
updated externally (e.g., by the `AirConditioningResource` when a new setpoint is applied) to simulate the effect of the
air‑conditioning system.

**Location:** `com.ambulance.resource.TemperatureSensorResource`

---

## Responsibilities

- Store the current cabin temperature (in Celsius).
- Handle `GET` requests with the current temperature in a consistent JSON format.
- Provide a setter (`setTemperature()`) for other resources or services to update the temperature, and notify any
  observing CoAP clients of the change via the `changed()` method.
- Remain a **read‑only** resource from the CoAP client’s perspective; PUT/POST are not overridden and will be rejected
  by Californium’s default handlers.

---

## Fields

| Field                | Type        | Visibility             | Description                                        |
|----------------------|-------------|------------------------|----------------------------------------------------|
| `LOG`                | `Logger`    | `private static final` | SLF4J logger for the class.                        |
| `currentTemperature` | `double`    | `private`              | Current temperature of the cabin (default `22.0`). |
| `cabinType`          | `CabinType` | `private final`        | The cabin this sensor belongs to.                  |

---

## Constructor

### `TemperatureSensorResource(String name, CabinType cabinType)`

```java
public TemperatureSensorResource(String name, CabinType cabinType);
```