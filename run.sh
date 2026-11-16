#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
echo "Starting Cafeteria Management System..."
mvn clean compile exec:java
