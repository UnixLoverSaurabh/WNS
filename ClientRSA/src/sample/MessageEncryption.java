package sample;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class MessageEncryption {

    private byte[] cipherText;

    public MessageEncryption(String message, PublicKey publicKeyServer) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] plainText = message.getBytes(StandardCharsets.UTF_8);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKeyServer);
        this.cipherText = cipher.doFinal(plainText);
    }

    public MessageEncryption(byte[] plainText, PublicKey publicKeyServer) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKeyServer);
        this.cipherText = cipher.doFinal(plainText);
    }

    public byte[] getCipherText() {
        return cipherText;
    }
}
