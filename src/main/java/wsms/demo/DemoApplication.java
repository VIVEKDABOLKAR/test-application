package wsms.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Test Web Application for WSMS Server Agent Monitoring
 * 
 * This is a simple Spring Boot application designed to be monitored by the server-agent.
 * It runs on port 5173 (default) and serves HTTP requests.
 * 
 * The server-agent will listen on port 4017 and forward requests from this application
 * to the backend for monitoring and metrics collection.
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
