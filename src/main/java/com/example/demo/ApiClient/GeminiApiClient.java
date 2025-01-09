package com.example.demo.ApiClient;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GeminiApiClient {

    private static final String BASE_URL = "https://api.gemini.com/v1"; // Gemini APIのベースURL
//    private static final String API_KEY = "AIzaSyD955ow1sfFRPEps0p1qKMRhvyoEXBKsZY"; // 取得したAPIキー

    private final RestTemplate restTemplate;

    public GeminiApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 例: 現在の暗号通貨の価格を取得するメソッド
    public String getCurrentPrice(String symbol) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/pubticker/" + symbol)
                .toUriString();

        // APIキーが必要な場合、ヘッダーに追加
        String response = restTemplate.getForObject(url, String.class);
        
        // Geminiのレスポンスを返す（JSON形式）
        return response;
    }
}
