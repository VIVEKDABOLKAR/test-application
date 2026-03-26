# Setup Guide: Test Web Application + Server Agent Monitoring

This guide walks you through setting up and running the **WSMS Test Web Application** with the **Server Agent** for HTTP request monitoring.

## 📋 Prerequisites

- **Java 21 or higher** installed
- **Maven 3.8+** installed
- **Windows/Linux/Mac** (guide assumes Windows, but works on all platforms)
- **Two terminals** (one for each application)

## ⚡ Quick Start (Windows)

### Option 1: Single Command Setup
Run the provided batch script:
```bash
cd test-web-application
./start-windows.bat
```

This builds and starts the test application on **http://localhost:5173**

### Option 2: Manual Setup

#### Step 1: Build and Start Test Web Application

**Terminal 1:**
```bash
cd test-web-application
mvn clean package
mvn spring-boot:run
```

Expected output:
```
Tomcat started on port(s): 5173 (http)
Started DemoApplication in X.XXX seconds
```

#### Step 2: Build Server Agent (if not already done)

**Terminal 2:**
```bash
cd server_agent-main
mvn clean package
```

Expected files:
- `target/server-agent-1.0.0.jar`

#### Step 3: Create Agent Config

In `server_agent-main/` create or verify `config.json`:

```json
{
  "serverId": "test-server-1",
  "serverName": "Test Web Application",
  "authToken": "demo-token-123",
  "backendUrl": "http://localhost:8080",
  "webServerHost": "::1",
  "webServerPort": 5173,
  "publishPort": 4017,
  "collectionInterval": "PT5S",
  "webApplicationMonitor": true
}
```

**Key Settings:**
- `webServerPort: 5173` - Must match test app port ✓
- `publishPort: 4017` - Agent listening port ✓
- `webApplicationMonitor: true` - Enable request logging ✓

#### Step 4: Start Server Agent

**Terminal 2 (newly available):**
```bash
cd server_agent-main
java -jar target/server-agent-1.0.0.jar --configPath config.json
```

Expected output:
```
[INFO] Agent initialized with config from: config.json
[INFO] Starting Connection Monitor on port 4017
[INFO] Agent started successfully
[INFO] Waiting for incoming connections...
```

## 🔍 Verify Integration

### Test 1: Access Application
```
HTTP://localhost:5173/
```
✅ Should see colorful homepage with buttons

### Test 2: Trigger HTTP Requests
1. Click **"Test Action 1"** button
2. Watch the response appear
3. Repeat with other buttons

### Test 3: Check Dashboard
```
HTTP://localhost:5173/dashboard
```
✅ Should show action count increasing

### Test 4: Verify Agent Logging
Check `server_agent-main/config.json` (last lines):

```json
"logs": [
  "[INFO_WEB] [GET] http://localhost:5173/ - Status: 200",
  "[INFO_WEB] [POST] http://localhost:5173/api/action?action=test-action - Status: 200",
  "[INFO_WEB] [GET] http://localhost:5173/api/metrics - Status: 200"
]
```

✅ If you see `[INFO_WEB]` entries → Agent is capturing requests!

## 📊 Understanding the Flow

```
┌─────────────────────────────────────┐
│  Browser / Client                   │
│  http://localhost:5173              │
└──────────────┬──────────────────────┘
               │
               │ HTTP Request
               ▼
┌─────────────────────────────────────┐
│  Test Web Application               │
│  :5173                              │
│  (Spring Boot)                      │
│  - Handles requests                 │
│  - Returns responses                │
└──────────────┬──────────────────────┘
               │
               │ Intercepted
               ▼
┌─────────────────────────────────────┐
│  Server Agent                       │
│  :4017 (listening port)             │
│  - Logs requests                    │
│  - Tracks metrics                   │
│  - Stores to config.json            │
└────────────────────────────────────┘
```

## 🧪 Testing Scenarios

### Scenario 1: Single Action
1. Click "Test Action 1"
2. Check agent logs for:
   - Request logged to config.json
   - Type: POST
   - Status: 200

### Scenario 2: Multiple Actions
1. Click buttons multiple times
2. Check dashboard request count increases
3. Verify each action logged in config.json

### Scenario 3: API Health Check
```bash
curl http://localhost:5173/api/health
```

Should return:
```json
{
  "status": "UP",
  "message": "Test Web Application is running",
  "javaVersion": "21.0.1",
  "memoryUsage": "512 MB"
}
```

### Scenario 4: Metrics Query
```bash
curl http://localhost:5173/api/metrics
```

Should return:
```json
{
  "actionCount": 3,
  "totalMemory": 536870912,
  "freeMemory": 268435456,
  "processorCount": 8
}
```

## 🛠️ Troubleshooting

### Problem: Port 5173 Already in Use
```bash
# Kill the process using port 5173
netstat -ano | findstr :5173
taskkill /PID <PID> /F

# Or change port in application.properties
# server.port=5174
```

### Problem: Agent Not Logging Requests
**Checklist:**
- [ ] webApplicationMonitor = true in config.json
- [ ] webServerPort = 5173 in config.json
- [ ] Agent is running and listening
- [ ] Check `config.json` has write permissions
- [ ] Firewall allows port 4017

**Debug:**
```bash
# Check if agent is listening
netstat -ano | findstr :4017
# Should show listening status
```

### Problem: "Failed to open log file: agent.log"
**Reason:** The agent is trying to write to config.json path (not agent.log)

**Solution:** This is expected behavior. Logs are appended to config.json.
Look for `[INFO_WEB]` entries in config.json.

### Problem: WebSocket Error (Red Exclamation Mark)
**This is NOT a problem for this demo!**
- ✅ Test app uses pure HTTP
- ✅ No WebSocket dependency
- ✅ All communication is REST-based
- This error would only appear if you tried to add WebSocket features

## 📝 Log Entries to Look For

In `config.json`, successful monitoring shows:

```
[INFO_WEB] [timestamp] [method] [url] - Status: [code]
```

Examples:
```
[INFO_WEB] 2026/03/26 10:30:45 GET http://localhost:5173/ - Status: 200
[INFO_WEB] 2026/03/26 10:30:46 POST http://localhost:5173/api/action?action=test-action - Status: 200
[INFO_WEB] 2026/03/26 10:30:47 GET http://localhost:5173/api/metrics - Status: 200
```

## 🚀 Performance Tuning

### Increase Monitoring Frequency
Edit `config.json`:
```json
"collectionInterval": "PT1S"  // Every 1 second instead of 5
```

### Disable Monitoring
Edit `config.json`:
```json
"webApplicationMonitor": false
```

### Change Agent Listen Port
Edit `config.json`:
```json
"publishPort": 8888  // Instead of 4017
```

## 📚 Additional Resources

- **Test App README**: `test-web-application/README.md`
- **Agent Config Guide**: `server_agent-main/CONFIG_GUIDE.md`
- **API Endpoints**: See test-web-application/README.md#api-endpoints

## ✅ Success Checklist

- [ ] Test application runs on 5173
- [ ] Agent starts and listens on 4017
- [ ] Can access http://localhost:5173
- [ ] Dashboard shows action count
- [ ] config.json has [INFO_WEB] entries
- [ ] HTTP requests are logged
- [ ] Multiple actions can be triggered
- [ ] No WebSocket errors (expected, not using WebSocket)

## 🎯 Next Steps

Once confirmed working:

1. **Generate Load**: Click buttons repeatedly to test monitoring
2. **Check Logs**: Review config.json for all captured requests
3. **Test Metrics**: Use API endpoints to verify data collection
4. **Backend Integration**: Connect to real backend at `backendUrl`

---

**Questions?** Check the individual README files in each folder for detailed documentation.

