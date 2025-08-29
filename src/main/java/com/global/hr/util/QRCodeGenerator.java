package com.global.hr.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class QRCodeGenerator {
    
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;
    private static final int BLACK = Color.BLACK.getRGB();
    private static final int WHITE = Color.WHITE.getRGB();
    
    /**
     * Generates a QR code image from the given text and returns it as a Base64 string
     * 
     * @param text The text to encode in the QR code (usually JWT token)
     * @param width Width of the QR code image
     * @param height Height of the QR code image
     * @return Base64 encoded PNG image of the QR code
     * @throws WriterException If QR code generation fails
     * @throws IOException If image conversion fails
     */
    public String generateQRCodeBase64(String text, int width, int height) throws WriterException, IOException {
        // Create QR code writer with error correction
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        
        // Set encoding hints for better quality
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        
        // Generate bit matrix
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        
        // Convert bit matrix to buffered image
        BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
            }
        }
        
        // Convert buffered image to Base64 string
        return convertImageToBase64(qrImage);
    }
    
    /**
     * Generates a QR code with default dimensions (300x300)
     */
    public String generateQRCodeBase64(String text) throws WriterException, IOException {
        return generateQRCodeBase64(text, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    /**
     * Converts a BufferedImage to Base64 encoded string
     */
    private String convertImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        byte[] imageBytes = baos.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
    
    /**
     * Validates if a string can be encoded as QR code
     */
    public boolean isValidQRContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        
        // QR codes can handle up to ~4,000 characters for alphanumeric content
        // JWT tokens are typically much smaller, but let's set a reasonable limit
        return content.length() <= 2000;
    }
}
