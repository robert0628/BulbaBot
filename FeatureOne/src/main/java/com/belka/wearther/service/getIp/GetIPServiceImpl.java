package com.belka.wearther.service.getIp;

import com.belka.wearther.models.IP;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Data
@Service
public class GetIPServiceImpl implements GetIPService {
    private final RestTemplate restTemplate;
    @Value("${ip.link}")
    private String link;

    public String getIP() {
        IP ip = restTemplate.getForObject(link, IP.class);
        if (ip == null || ip.getIp() == null) {
            throw new RuntimeException("couldn't read this IP");
        }
        return ip.getIp();
    }

}
