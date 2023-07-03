package com.belka.QR.Services;

/**
 * Service for converting text to QR-code
 */
public interface QRService {
    /**
     * @param messageText - user's text
     * @return link to the QR-code
     */
    String getQRLink(String messageText);
}
