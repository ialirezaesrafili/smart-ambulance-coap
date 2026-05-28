# Temperature sensor (cockpit)

```shell
coap-client -m get coap://localhost:5683/temp
```

# Air‑conditioning target (rear cabin)

```shell
coap-client -m get coap://localhost:5684/ac
```

# HMI (cockpit)

```shell
coap-client -m get coap://localhost:5683/hmi
```

# PUT – update resources

## Set temperature (driver) – cockpit

```shell
echo -n '{"target_temperature": 24.0, "role": "driver"}' | \
coap-client -m put coap://localhost:5683/ac -f -
```

## Set temperature (reliever) – rear cabin only (will fail on cockpit)
```shell
echo -n '{"target_temperature": 19.0, "role": "reliever"}' | \
coap-client -m put coap://localhost:5684/ac -f -
```

## Update HMI emergency info (driver)
```shell
echo -n '{"role": "driver", "emergency_level": 3, "event_location": 4}' | \
coap-client -m put coap://localhost:5683/hmi -f -
```

## Update HMI GPS coordinates

```shell
echo -n '{"role": "gps", "gps_coordinates": "45.4642,9.1900"}' | \
coap-client -m put coap://localhost:5683/hmi -f -
```



# Full Test

```shell
# 1. Read initial temperatures
coap-client -m get coap://localhost:5683/temp
coap-client -m get coap://localhost:5684/temp

# 2. Driver sets temperature to 24°C in both cabins
echo -n '{"target_temperature":24.0,"role":"driver"}' | coap-client -m put coap://localhost:5683/ac -f -
echo -n '{"target_temperature":24.0,"role":"driver"}' | coap-client -m put coap://localhost:5684/ac -f -

# 3. Reliever tries to set cockpit temperature (must fail)
echo -n '{"target_temperature":18.0,"role":"reliever"}' | coap-client -m put coap://localhost:5683/ac -f -

# 4. Reliever sets rear cabin temperature (succeeds)
echo -n '{"target_temperature":19.0,"role":"reliever"}' | coap-client -m put coap://localhost:5684/ac -f -

# 5. Driver updates HMI emergency info on both cabins
echo -n '{"role":"driver","emergency_level":3,"event_location":4}' | coap-client -m put coap://localhost:5683/hmi -f -
echo -n '{"role":"driver","emergency_level":3,"event_location":4}' | coap-client -m put coap://localhost:5684/hmi -f -

# 6. GPS updates coordinates on both cabins
echo -n '{"role":"gps","gps_coordinates":"45.4642,9.1900"}' | coap-client -m put coap://localhost:5683/hmi -f -
echo -n '{"role":"gps","gps_coordinates":"45.4642,9.1900"}' | coap-client -m put coap://localhost:5684/hmi -f -

# 7. Verify final HMI state
coap-client -m get coap://localhost:5683/hmi

```