# Smart Ambulance Management System 

## Overview

This project focuses on the design and simulation of a **Smart Ambulance Management System** using the **CoAP (Constrained Application Protocol)** and the **Eclipse Californium Framework**.

The system models a smart ambulance equipped with heterogeneous virtual sensors and smart control modules operating in a simulated environment. The project emphasizes communication between distributed entities through CoAP clients and servers while supporting real-time monitoring and remote control functionalities.

---

# System Architecture

The smart ambulance, denoted as **A**, is designed for emergency response operations and contains multiple interconnected components.

## Main Actors

The system includes the following entities:

| Entity | Description            | Role        |
| ------ | ---------------------- | ----------- |
| **D**  | Driver                 | CoAP Client |
| **R**  | Reliever               | CoAP Client |
| **G**  | GPS Receiver           | CoAP Client |
| **AC** | Cockpit Environment    | CoAP Server |
| **AR** | Rear Cabin Environment | CoAP Server |

---

# Functional Components

Both ambulance environments (**AC** and **AR**) host a set of CoAP resources.

## 1. Temperature Sensor (`ST`)

The temperature sensor provides the current temperature of the corresponding ambulance environment.

### Supported Operations

| Method | Description                           |
| ------ | ------------------------------------- |
| `GET`  | Returns the current temperature value |

### Notes

* The returned temperature may vary according to the state of the air conditioning system.

---

## 2. Air Conditioning Controller (`SA`)

The air conditioning controller manages the target temperature inside the ambulance environments.

### Supported Operations

| Method | Description                                         |
| ------ | --------------------------------------------------- |
| `GET`  | Returns the currently configured target temperature |
| `PUT`  | Updates the target temperature                      |

### Access Control Rules

| Operator     | AC (Cockpit)  | AR (Rear Cabin) |
| ------------ | ------------- | --------------- |
| Driver (D)   | ✅ Allowed     | ✅ Allowed       |
| Reliever (R) | ❌ Not Allowed | ✅ Allowed       |

---

## 3. Human Machine Interface (`SH`)

The Human Machine Interface represents a smart tablet/dashboard used by operators.

### Supported Operations

| Method | Description                                                  |
| ------ | ------------------------------------------------------------ |
| `GET`  | Returns emergency level, event location, and GPS coordinates |
| `PUT`  | Updates emergency level and event location                   |

### Emergency Levels

| Value | Meaning |
| ----- | ------- |
| `1`   | Low     |
| `2`   | Medium  |
| `3`   | High    |

### Event Locations

| Value | Meaning      |
| ----- | ------------ |
| `1`   | Street       |
| `2`   | Workplace    |
| `3`   | Public Space |
| `4`   | Home         |

---

# GPS Integration

The GPS receiver (**G**) continuously tracks the ambulance position and updates both HMIs with:

* Latitude
* Longitude

### GPS Permissions

| Entity           | Permission                       |
| ---------------- | -------------------------------- |
| GPS Receiver (G) | Can update GPS coordinates only  |
| Driver (D)       | Can update emergency information |
| Reliever (R)     | Can update emergency information |

---

# Synchronization Constraints

To ensure consistency across the system:

* Updates performed by **D** or **R** on emergency level and event location **must be applied simultaneously** on:

  * `SH` in `AC`
  * `SH` in `AR`

* GPS updates from `G` must also synchronize both HMIs.

---

# Communication Model

The project relies on the **CoAP protocol** for lightweight machine-to-machine communication.

## Technologies Used

| Technology              | Purpose                            |
| ----------------------- | ---------------------------------- |
| **CoAP**                | Lightweight communication protocol |
| **Eclipse Californium** | Java framework for CoAP            |
| **Java**                | System implementation              |
| **Virtual Sensors**     | Environment simulation             |

---

# Example CoAP Resources

## Cockpit (`AC`)

```text
/co ckpit/temperature
/cockpit/airconditioner
/cockpit/hmi
```

## Rear Cabin (`AR`)

```text
/rear/temperature
/rear/airconditioner
/rear/hmi
```

---

# Example Requests

## Get Current Temperature

```http
GET coap://AC/temperature
```

## Set Target Temperature

```http
PUT coap://AR/airconditioner
Payload: 22
```

## Update Emergency Information

```http
PUT coap://AC/hmi
Payload:
{
  "emergencyLevel": 3,
  "eventLocation": 1
}
```

---

# Project Objectives

The main goals of this project are:

* Simulate a smart ambulance ecosystem
* Implement CoAP-based communication
* Manage distributed virtual sensors
* Synchronize critical information between ambulance modules
* Enforce access control policies
* Model real-time monitoring and remote actuation

---

# Expected Features

* Real-time temperature monitoring
* Smart air conditioning management
* GPS-based ambulance tracking
* Emergency information synchronization
* Role-based access permissions
* Lightweight IoT communication

---

# Conclusion

This project demonstrates how IoT technologies and lightweight communication protocols such as CoAP can be used to build intelligent emergency management systems.

The simulated smart ambulance environment provides a practical scenario for studying:

* distributed systems,
* IoT communication,
* resource synchronization,
* and smart healthcare infrastructure.
