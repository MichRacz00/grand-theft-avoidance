package model;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * @author Antoine Moghaddar
 */

public class Hashing {

    private static final String saltSecretKey = "Yj5059AJz9w7DA1zk6r7";

    public static String SHA512_encoder(String value) {
        try {
            // Get an hmac_sha1 secretkey from the raw secretkey bytes
            byte[] keyBytes = Hashing.saltSecretKey.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA512");

            // Get an hmac_sha512 Mac instance and initialize with the signing
            // secretkey
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes());

            // Convert raw bytes to Hex
            byte[] hexBytes = new Hex().encode(rawHmac);

            // Covert array of Hex bytes to a String
            return new String(hexBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}