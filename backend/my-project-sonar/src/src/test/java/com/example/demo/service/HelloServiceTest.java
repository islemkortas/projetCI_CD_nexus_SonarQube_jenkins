package com.example.demo.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HelloServiceTest {

    @Test
    void testGetMessage() {
        HelloService service = new HelloService();
        String result = service.getMessage();
        assertEquals("Hello from Service ! result=5", result);
    }
}
