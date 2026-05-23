#!/bin/bash

echo "Cleaning and compiling project..."

mvn clean compile

if [ $? -eq 0 ]; then
    echo "Starting CoAP server..."
    mvn exec:java
else
    echo "Build failed"
fi