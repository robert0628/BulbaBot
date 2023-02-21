package com.belka;

import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Data
public class ServiceStackOverFlow implements StackOverFlow {
    private final RestTemplate restTemplate;
    private final String link = "https://api.stackexchange.com/docs/articles#page=1&pagesize=3&order=desc&sort=activity&filter=default&site=stackoverflow";

    public ServiceStackOverFlow(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void fuu() {
        restTemplate.getForObject(link, ListPosts.class);
    }
}
