package com.belka.QR.Serices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QRServiceImpl implements QRService {
    @Value("${qr.link}")
    private String link;

    @Override
    public String getQRLink(String messageText) {
        String text = messageText.substring(6);
        return this.link + text;
    }
}
