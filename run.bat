@echo off
cd /d "%~dp0"
echo Starting Cafeteria Management System...
mvn clean compile exec:java
if errorlevel 1 pause
