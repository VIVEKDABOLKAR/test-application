# WSMS Test Web Application

A **simple Spring Boot demo application** designed to be monitored by the **WSMS Server Agent**.

## Overview

This test web application demonstrates how to effectively monitor HTTP-based applications using the server-agent. It serves as a proof-of-concept for:

- ✅ **HTTP Request Monitoring** - Track all HTTP requests without WebSocket complications
- ✅ **No WebSocket Issues** - Pure REST API design avoids the WebSocket problems encountered with React
- ✅ **Performance Metrics** - Built-in endpoints for CPU, memory, and action tracking
- ✅ **Real-time Logs** - Dashboard shows all monitored HTTP activity

## Architecture

```
Test Web Application (Port 5173)
    └── HTTP Requests
         └── Server Agent (Port 4017)
              └── Intercepts & Monitors
                   └── Logs to config.json
```

## Requirements

- Java 21 or higher
- Maven 3.8+
- Spring Boot 3.1.5

## Installation & Setup

### 1. Build the Application

```bash
cd test-web-application
mvn clean package
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

Or after building:

```bash
java -jar target/test-web-application-1.0.0.jar
```

The application will start on **http://localhost:5173**

## API Endpoints

### Static Pages
- **GET** `/` - Home page with interactive demo
- **GET** `/dashboard` - Dashboard with request logs and statistics

### REST API Endpoints

#### **GET** `/api/health`
Returns application health status
```json
{
  "status": "UP",
  "message": "Test Web Application is running",
  "timestamp": "2026-03-26T10:30:45.123",
  "javaVersion": "21.0.1",
  "memoryUsage": "512 MB"
}
```

#### **GET** `/api/metrics`
Returns current application metrics
```json
{
  "actionCount": 5,
  "timestamp": 1711436445123,
  "totalMemory": 536870912,
  "freeMemory": 268435456,
  "processorCount": 8
}
```

#### **POST** `/api/action?action=<action-name>`
Performs a test action and logs it
```json
{
  "success": true,
  "message": "Action 'test-action' completed successfully",
  "data": "[2026-03-26 10:30:45] Action performed: test-action"
}
```

#### **GET** `/api/logs`
Retrieves all logged actions as array
```json
[
  "[2026-03-26 10:30:45] Action performed: test-action",
  "[2026-03-26 10:30:50] Action performed: data-sync"
]
```

#### **POST** `/api/logs/clear`
Clears all stored logs
```json
{
  "success": true,
  "message": "Cleared 5 log entries",
  "data": null
}
```

## Using with Server Agent

### Configuration

Update your server-agent `config.json` to monitor this application:

```json
{
  "serverId": "test-server-1",
  "serverName": "Test Web App",
  "webServerHost": "::1",
  "webServerPort": 5173,
  "publishPort": 4017,
  "backendUrl": "http://localhost:8080",
  "webApplicationMonitor": true,
  "collectionInterval": 5
}
```

### Running Server Agent

```bash
cd ../server_agent-main
java -jar target/server-agent-1.0.0.jar --configPath config.json
```

The server-agent will:
1. Start listening on port 4017
2. Monitor HTTP requests to localhost:5173
3. Log all activity to its config file
4. Send metrics to the backend

### Testing the Integration

1. **Start the test web application**
   ```bash
   mvn spring-boot:run
   ```

2. **Start the server-agent** (in another terminal)
   ```bash
   java -jar server-agent-1.0.0.jar --configPath config.json
   ```

3. **Access the home page** at http://localhost:5173

4. **Click action buttons** - Each click generates:
   - HTTP POST request to `/api/action`
   - Server-agent intercepts the request on port 4017
   - Request is logged to server-agent's config.json
   - Response is returned to the browser

5. **View dashboard** at http://localhost:5173/dashboard
   - Shows all logged actions
   - Displays request statistics

6. **Check server-agent logs** in config.json
   - Look for:
     - `[INFO_WEB]` entries for HTTP request logs
     - Request counts and metrics

## Key Features for Agent Monitoring

### Pure HTTP Communication
- No WebSocket connections
- All communication via standard HTTP/REST
- Predictable request patterns for monitoring
- Easy to debug and trace

### Multiple Request Types
- **GET requests** - Page loads, health checks, metrics queries
- **POST requests** - Action triggers, log clearing
- **Static resources** - HTML, CSS (minimal)

### Structured Response Format
- All API responses are JSON
- Consistent error handling
- Includes timestamp and status information

### Performance Tracking
- Built-in simulated delays (100-500ms per action)
- Memory usage statistics
- CPU and processor information
- Request counting

## Project Structure

```
test-web-application/
├── pom.xml                          # Maven configuration
├── README.md                        # This file
└── src/
    └── main/
        ├── java/wsms/demo/
        │   ├── DemoApplication.java             # Spring Boot entry point
        │   └── controller/
        │       └── DemoController.java          # REST endpoints
        └── resources/
            ├── application.properties           # Spring configuration
            ├── static/
            │   └── index.html                   # Home page
            └── templates/
                └── dashboard.html               # Dashboard page
```

## Why Spring Boot Instead of React?

| Aspect | Spring Boot Demo | React |
|--------|================|-------|
| WebSocket | ❌ Not used | ⚠️ Has issues with agent |
| HTTP Monitoring | ✅ Native | ✅ Works via proxy |
| Complexity | ✅ Simple | ⚠️ More complex |
| Static Serving | ✅ Built-in | ✅ Requires build |
| Agent Integration | ✅ Seamless | ⚠️ Websocket conflicts |

## Monitoring Points

The server-agent will track:

1. **HTTP GET Requests**
   - Page loads (/)
   - Dashboard access
   - Health checks
   - Metrics queries

2. **HTTP POST Requests**
   - Action triggers
   - Log clearing

3. **Response Metrics**
   - Status codes
   - Response times
   - Request count

4. **Application State**
   - Memory usage
   - Process information
   - Health status

## Troubleshooting

### Port 5173 Already in Use
```bash
# Either stop the conflicting process or change the port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=5174"
```

### Server Agent Not Intercepting Requests
1. Verify webServerPort matches: 5173
2. Verify webApplicationMonitor is true in config.json
3. Check that agent is listening on port 4017
4. Check Windows Firewall allows the ports

### Application Won't Start
```bash
# Make sure Java 21+ is installed
java -version

# Check for conflicting processes
netstat -ano | findstr :5173
```

## Development Notes

- Toggle `webApplicationMonitor` in config.json to enable/disable request logging
- Modify `collectionInterval` in config.json to change metrics collection frequency
- All logs are stored in memory (clears on restart)
- For production, implement persistent storage (database)

## Future Enhancements

- [ ] Database integration for persistent logs
- [ ] More realistic workload simulation
- [ ] Admin API for configuration changes
- [ ] Metrics export (Prometheus format)
- [ ] Load testing endpoints
- [ ] WebSocket optional support (isolated from main monitoring)

## License

Part of WSMS (Web Server Management System) project.

---

**Created for:** Server-Agent Monitoring Demonstration  
**Version:** 1.0.0  
**Last Updated:** March 26, 2026
