package wsms.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for handling demo application requests
 * 
 * All HTTP requests through this controller will be intercepted and monitored by server-agent.
 * This demonstrates how the agent tracks:
 * - HTTP request counts
 * - Request types (GET, POST, etc.)
 * - Response times
 */
@Controller
@RequestMapping("/")
public class DemoController {

    // Sample data store (in-memory)
    private static List<String> logs = new ArrayList<>();

    @GetMapping
    public String index(Model model) {
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("title", "WSMS Test Web Application");
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("requestCount", getLogs().size());
        model.addAttribute("logs", getLogs());
        return "dashboard";
    }

    @PostMapping("/api/action")
    @ResponseBody
    public ApiResponse performAction(@RequestParam String action) {
        String logEntry = "[" + LocalDateTime.now() + "] Action performed: " + action;
        logs.add(logEntry);
        
        // Simulate some processing
        try {
            Thread.sleep(100 + (long) (Math.random() * 400)); // 100-500ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return new ApiResponse(
            true,
            "Action '" + action + "' completed successfully",
            logEntry
        );
    }

    @GetMapping("/api/metrics")
    @ResponseBody
    public MetricsResponse getMetrics() {
        return new MetricsResponse(
            logs.size(),
            System.currentTimeMillis(),
            Runtime.getRuntime().totalMemory(),
            Runtime.getRuntime().freeMemory(),
            Runtime.getRuntime().availableProcessors()
        );
    }

    @GetMapping("/api/logs")
    @ResponseBody
    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    @PostMapping("/api/logs/clear")
    @ResponseBody
    public ApiResponse clearLogs() {
        int count = logs.size();
        logs.clear();
        return new ApiResponse(true, "Cleared " + count + " log entries", null);
    }

    @GetMapping("/api/health")
    @ResponseBody
    public HealthResponse health() {
        return new HealthResponse(
            "UP",
            "Test Web Application is running",
            LocalDateTime.now(),
            System.getProperty("java.version"),
            Runtime.getRuntime().totalMemory() / (1024 * 1024) + " MB"
        );
    }

    // Inner response classes
    public static class ApiResponse {
        public boolean success;
        public String message;
        public String data;

        public ApiResponse(boolean success, String message, String data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getData() { return data; }
    }

    public static class MetricsResponse {
        public int actionCount;
        public long timestamp;
        public long totalMemory;
        public long freeMemory;
        public int processorCount;

        public MetricsResponse(int actionCount, long timestamp, long totalMemory, long freeMemory, int processorCount) {
            this.actionCount = actionCount;
            this.timestamp = timestamp;
            this.totalMemory = totalMemory;
            this.freeMemory = freeMemory;
            this.processorCount = processorCount;
        }

        public int getActionCount() { return actionCount; }
        public long getTimestamp() { return timestamp; }
        public long getTotalMemory() { return totalMemory; }
        public long getFreeMemory() { return freeMemory; }
        public int getProcessorCount() { return processorCount; }
    }

    public static class HealthResponse {
        public String status;
        public String message;
        public LocalDateTime timestamp;
        public String javaVersion;
        public String memoryUsage;

        public HealthResponse(String status, String message, LocalDateTime timestamp, String javaVersion, String memoryUsage) {
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
            this.javaVersion = javaVersion;
            this.memoryUsage = memoryUsage;
        }

        public String getStatus() { return status; }
        public String getMessage() { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getJavaVersion() { return javaVersion; }
        public String getMemoryUsage() { return memoryUsage; }
    }
}
