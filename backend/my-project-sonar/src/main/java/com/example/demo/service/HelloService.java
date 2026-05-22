package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String getMessage() {
        String prefix = "Hello";
        int total = 10;
        int divisor = 2;
        int result = total / divisor;
        return prefix + " from Service ! result=" + result;
    }
}
