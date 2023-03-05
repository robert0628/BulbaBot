package com.belka.wearther.service.geo;

import com.belka.wearther.models.Geo;
import com.belka.wearther.service.getIp.GetIPService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Data
@Service
public class GeoFromIPServiceImpl implements GeoFromIPService {

    @Value("${geo-ip.link}")
    private String link;
    private final GetIPService getIPService;
    private final RestTemplate restTemplate;

    private String getLink() {
        return link + getIPService.getIP();
    }

    public String getCityName() {
        Geo geo = restTemplate.getForObject(getLink(), Geo.class);
        if (geo == null || geo.getCity() == null) {
            throw new RuntimeException("couldn't read this City");
        }
        return geo.getCity();
    }
}
