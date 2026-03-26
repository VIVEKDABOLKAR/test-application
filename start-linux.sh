#!/bin/bash
# Quick start script for WSMS Test Web Application on Linux/Mac

echo ""
echo "========================================"
echo "WSMS Test Web Application - Quick Start"
echo "========================================"
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven not found"
    echo "Please install Maven or add it to PATH"
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java not found"
    echo "Please install Java 21 or higher"
    exit 1
fi

echo "[1/3] Checking Java version..."
java -version
echo ""

echo "[2/3] Building application..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "ERROR: Build failed"
    exit 1
fi

echo ""
echo "[3/3] Starting application..."
echo "Application will run on: http://localhost:5173"
echo ""
echo "Press Ctrl+C to stop the application"
echo ""

java -jar target/test-web-application-1.0.0.jar

