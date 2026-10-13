package vente.ticket.evenement.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vente.ticket.evenement.exception.BusinessException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class QrCodeService {

    private final String hmacSecret;

    public QrCodeService(@Value("${app.qr.hmac-secret}") String hmacSecret) {
        this.hmacSecret = hmacSecret;
    }

    public String sign(String codeUnique, Long eventId, Long userId) {
        String payload = codeUnique + "|" + eventId + "|" + userId;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(hmacSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new BusinessException("Impossible de signer le ticket");
        }
    }

    public boolean verify(String codeUnique, Long eventId, Long userId, String signature) {
        String expected = sign(codeUnique, eventId, userId);
        return expected.equalsIgnoreCase(signature);
    }

    public String generateQrBase64(String code, Long eventId, String signature) {
        String content = "{\"code\":\"" + code + "\",\"eventId\":" + eventId + ",\"sig\":\"" + signature + "\"}";
        try {
            BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 300, 300);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            throw new BusinessException("Impossible de générer le QR code");
        }
    }
}
