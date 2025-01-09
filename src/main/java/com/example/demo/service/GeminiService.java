package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.ApiClient.GeminiApiClient;

@Service
public class GeminiService {

    private final GeminiApiClient geminiApiClient;

    public GeminiService(GeminiApiClient geminiApiClient) {
        this.geminiApiClient = geminiApiClient;
    }

    public void displayCurrentPrice(String symbol) {
        String response = geminiApiClient.getCurrentPrice(symbol);
        System.out.println("Current price of " + symbol + ": " + response);
    }
}

