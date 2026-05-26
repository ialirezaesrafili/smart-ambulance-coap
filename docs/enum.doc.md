# ServerState

**Package:** `com.ambulance.enums`  
**Language:** Java  
**Type:** `enum`

Represents the complete lifecycle of an Ambulance CoAP server.  
Each state carries a numeric status code and a human‑readable message, making it easy to trace server behaviour,
integrate with monitoring systems, and map states to CoAP‑like response semantics.

---

## Overview

The `ServerState` enum models every possible phase of the server’s existence – from creation to shutdown, including
transient states and error conditions. It is used throughout the Ambulance CoAP server component to:

- Track the current server lifecycle status.
- Provide consistent status codes for logging and external observability.
- Enable safe, state‑aware decision making (e.g., “Is the server ready to process requests?”).

---

## Enum Constants

| Constant   | Code | Message              | Description                                                                |
|------------|------|----------------------|----------------------------------------------------------------------------|
| `CREATED`  | 0    | `Server created`     | The server object exists but has not been started. No sockets bound yet.   |
| `STARTING` | 100  | `Server is starting` | The server is initialising (binding ports, registering resources).         |
| `RUNNING`  | 200  | `Server is running`  | Fully operational – able to accept and process CoAP requests.              |
| `STOPPING` | 300  | `Server is stopping` | Graceful shutdown in progress (unbinding, draining requests, cleaning up). |
| `STOPPED`  | 301  | `Server stopped`     | Server has completed shutdown; no longer listening.                        |
| `ERROR`    | 400  | `Server error`       | A critical error occurred; server may need manual intervention or restart. |

### Code Semantics

The numeric codes are loosely inspired by CoAP response classes:

- `200` – success (RUNNING, analogous to CoAP 2.xx).
- `400` – error (ERROR, analogous to CoAP 4.xx client/server error).
- `100`, `300`, `301` – informational / redirection style, mirroring startup and shutdown phases.
- `0` – a neutral code for the initial `CREATED` state, before any lifecycle action begins.

---

# EventLocation

**Package:** `com.ambulance.enums`  
**Language:** Java  
**Type:** `enum`

Represents the type of location where an emergency event occurs.  
Each constant maps a predefined category (Street, Workplace, Public Space, Home) to a numeric code as specified in the
project requirements (1–4). This enum provides a consistent and type‑safe way to classify incident locations throughout
the Ambulance system.

---

## Overview

The `EventLocation` enum defines the four possible emergency location types. It is used wherever an incident’s
environment needs to be recorded, transmitted, or displayed, such as:

- Incoming emergency reports.
- Database storage of incident details.
- CoAP message payloads sent between ambulance devices and the central server.
- User interfaces and logging.

By using an enum instead of raw integers, the codebase avoids magic numbers and ensures only valid location codes are
used.

---

## Enum Constants

| Constant       | Code | Label          | Description                                                |
|----------------|------|----------------|------------------------------------------------------------|
| `STREET`       | 1    | `Street`       | An emergency occurring on a public road or street.         |
| `WORKPLACE`    | 2    | `Workplace`    | An emergency at a place of work (office, factory, etc.).   |
| `PUBLIC_SPACE` | 3    | `Public Space` | An emergency in a publicly accessible area (park, square). |
| `HOME`         | 4    | `Home`         | An emergency at a private residence.                       |

The codes (1–4) are fixed and correspond directly to the specification. They can be used as compact representations in
data protocols or storage.

---

# EmergencyLevel

**Package:** `com.ambulance.enums`  
**Language:** Java  
**Type:** `enum`

Represents the severity level of an emergency, as set on the Human‑Machine Interface (HMI) of the ambulance system.  
Each constant is mapped to a numeric code (1–3) defined in the project specification, allowing a compact, type‑safe
representation of severity across the entire application.

---

## Overview

The `EmergencyLevel` enum defines the three possible severity ratings for an incident: **Low**, **Medium**, and **High
**. It is used wherever the criticality of an emergency needs to be recorded, compared, transmitted, or displayed, such
as:

- The ambulance HMI for crew input.
- CoAP message payloads exchanged with the central server.
- Database records of incidents.
- Prioritisation algorithms and alerting mechanisms.

Using an enum prevents invalid severity values, replaces error‑prone integer constants, and adds convenience methods for
severity comparisons.

---

## Enum Constants

| Constant | Code | Label    | Description                                                             |
|----------|------|----------|-------------------------------------------------------------------------|
| `LOW`    | 1    | `Low`    | Minor emergency; minimal resource allocation needed.                    |
| `MEDIUM` | 2    | `Medium` | Moderate emergency; requires prompt attention but not life‑threatening. |
| `HIGH`   | 3    | `High`   | Critical emergency; immediate response and maximum resources required.  |

The numeric codes (1 = Low, 2 = Medium, 3 = High) are fixed and match the project specification exactly. They inherently
define an ordinal severity scale, where a higher code indicates a more severe emergency.

---

# CabinType

**Package:** `com.ambulance.enums`  
**Language:** Java  
**Type:** `enum`

Represents the two distinct operating environments inside the smart ambulance: the **cockpit** (front cabin) and the *
*rear cabin** (medical compartment).  
This enum is used for server instantiation, permission verification, and resource addressing, ensuring that requests and
actions are correctly scoped to the appropriate cabin.

---

## Overview

The `CabinType` enum defines the two physical zones within the ambulance where CoAP servers and HMI interfaces may
operate:

- **COCKPIT** – where the driver is located; typically associated with driving controls, navigation, and cabin
  environment settings.
- **REAR_CABIN** – the medical treatment area; primarily managed by the reliever (paramedic), focusing on patient
  monitoring and medical equipment.

The enum provides a human‑readable display name, a compact short code matching the project notation (`AC` for Cockpit,
`AR` for Rear Cabin), and a built‑in authorization method for temperature setpoint adjustments based on the user role.

---

## Enum Constants

| Constant     | Short Code | Display Name | Description                                                     |
|--------------|------------|--------------|-----------------------------------------------------------------|
| `COCKPIT`    | `AC`       | `Cockpit`    | The front cabin where the driver operates the vehicle.          |
| `REAR_CABIN` | `AR`       | `Rear Cabin` | The rear medical cabin where the reliever tends to the patient. |

The short codes (`AC` = Ambulance Cockpit, `AR` = Ambulance Rear) are fixed and correspond to the project’s naming
convention. They are useful for forming unique resource paths or server identifiers.

---

