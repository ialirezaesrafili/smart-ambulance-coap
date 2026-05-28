# Client structures

## Client.java – Abstract CoAP Client Base

### Purpose

This abstract class serves as the **common parent** for the three specific CoAP client roles:

- `DriverClient`
- `RelieverClient`
- `GpsClient`

It encapsulates the core CoAP communication logic (`GET` and `PUT`), JSON payload construction, and device management.
By centralising these operations, the concrete sub‑classes remain focused on their role‑specific behaviours while
reusing a consistent, well‑tested communication layer.

## Key Fields

| Field        | Type         | Description                                                                                                                                           |
|--------------|--------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| `role`       | `String`     | Immutable identifier of the actor, one of `"driver"`, `"reliever"`, `"gps"`. Used in every outgoing PUT request to enforce server‑side authorisation. |
| `device`     | `Device`     | Wrapper around the target CoAP resource’s IP, port, and path. It can be changed at runtime to redirect the client.                                    |
| `coapClient` | `CoapClient` | Californium client that performs the actual network requests. Recreated when the device is changed.                                                   |
| `log`        | `Logger`     | SLF4J logger for debugging and error tracking.                                                                                                        |

## Constructor

```java
public abstract Client(String role, Device device);
```

Creates a new CoAP client with a fixed role and target device.

**Parameters:**

- `role` – the actor’s identity (`"driver"`, `"reliever"`, `"gps"`).
- `device` – a `Device` instance describing the CoAP resource (host, port, resource path).

**Details:**

- The `role` is stored immutably – it cannot be changed after construction.
- A Californium `CoapClient` is immediately built from `device.getUri()`.
- The logger is initialized with the actual subclass name.

---

# Device.java – CoAP Endpoint Descriptor

## Overview

`Device` is a simple Plain Old Java Object (POJO) that describes a reachable CoAP resource endpoint. It encapsulates the
necessary network parameters (IP address, port, and resource path) and provides a human-readable identifier. This class
is used throughout the client layer to define the target of each CoAP request.

**Location:** `com.ambulance.client.Device`

## Fields

| Field       | Type     | Visibility | Description                                                   |
|-------------|----------|------------|---------------------------------------------------------------|
| `deviceId`  | `String` | `private`  | A user-defined label for the device (e.g., `"driver-ac-AC"`). |
| `ipAddress` | `String` | `private`  | The IP address or hostname of the CoAP server.                |
| `port`      | `int`    | `private`  | The UDP port on which the CoAP server listens.                |
| `resource`  | `String` | `private`  | The resource path (e.g., `"temp"`, `"ac"`, `"hmi"`).          |

## Constructor

### `Device(String deviceId, String ipAddress, int port, String resource)`

```java
public Device(String deviceId, String ipAddress, int port, String resource);
```

# DriverClient.java – Driver Role Client

## Overview

`DriverClient` is the CoAP client representing the **driver (D)** in the smart ambulance simulation. It extends the
abstract `Client` with the fixed role `"driver"`. This client is authorised to set the temperature in both cabins (
cockpit and rear cabin), update emergency information on the HMI, and read sensor/HMI values.

**Location:** `com.ambulance.client.DriverClient`

---

## Inheritance

The `DriverClient` inherits the common CoAP communication methods (`get()`, `put()`, `baseJson()`) and device management
from `Client`. Its distinct behaviour is provided through domain‑specific methods that construct the appropriate JSON
payloads and invoke `put()` or `get()`.

---

## Constructor

### `DriverClient(Device device)`

```java
public DriverClient(Device device);
```

# GpsClient.java – GPS Role Client

## Overview

`GpsClient` is the CoAP client representing the **GPS receiver (G)** in the smart ambulance simulation. It extends the
abstract `Client` with the fixed role `"gps"`. This client is only permitted to update the GPS coordinates on the HMI
and read the current HMI state.

**Location:** `com.ambulance.client.GpsClient`

---

## Inheritance

The `GpsClient` inherits common CoAP communication methods (`get()`, `put()`, `baseJson()`) and device management from
`Client`. Its domain‑specific methods are limited to GPS updates and HMI reads.

---

## Constructor

### `GpsClient(Device device)`

```java
public GpsClient(Device device);
```

# RelieverClient.java – Reliever Role Client

## Overview

`RelieverClient` is the CoAP client representing the **reliever (R)** in the smart ambulance simulation. It extends the
abstract `Client` with the fixed role `"reliever"`. According to the system specification, the reliever is allowed to
set the temperature **only in the rear cabin (AR)** and can update emergency information on both HMIs.

**Location:** `com.ambulance.client.RelieverClient`

---

## Inheritance

The `RelieverClient` inherits common CoAP communication methods (`get()`, `put()`, `baseJson()`) and device management
from `Client`. Its domain‑specific methods mirror those of `DriverClient`, but the server‑side authorisation will reject
temperature updates if the target is the cockpit (AC).

---

## Constructor

### `RelieverClient(Device device)`

```java
public RelieverClient(Device device);
```
