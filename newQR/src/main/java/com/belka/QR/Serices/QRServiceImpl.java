package com.belka.QR.Serices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QRServiceImpl implements QRService {

    private final int PREFIX_LENGTH = 6;
    @Value("${qr.link}")
    private String link;

    @Override
    public String getQRLink(String messageText) {
        String text = messageText.substring(PREFIX_LENGTH);
        return this.link + text;
    }
}
