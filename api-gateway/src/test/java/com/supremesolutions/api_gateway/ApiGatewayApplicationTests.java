package com.supremesolutions.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = ApiGatewayApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles("test") // ✅ Use our test config file
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring Boot context starts successfully
    }
}
