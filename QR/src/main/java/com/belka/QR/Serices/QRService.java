package com.belka.QR.Serices;

/**
 * Service for converting message to QR-code
 */
public interface QRService {
    /**
     * @param messageText - user's text
     * @return link to the QR-code
     */
    String getQRLink(String messageText);
}
