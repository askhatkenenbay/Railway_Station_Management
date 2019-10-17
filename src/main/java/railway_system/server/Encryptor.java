package railway_system.server;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Encryptor {
    private Encryptor(){

    }
    public static String encrypInput(String plaintext) {
        if (plaintext == null || plaintext.isBlank()) {
            return "";
        }
        byte[] bytes;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(plaintext.getBytes(StandardCharsets.UTF_8));
            bytes = messageDigest.digest();
            return Arrays.toString(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
