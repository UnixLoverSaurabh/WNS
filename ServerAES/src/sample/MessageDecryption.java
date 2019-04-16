package sample;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MessageDecryption {

    private String plainText;
    private byte[] plainFile;

    public MessageDecryption(byte[] cipherText, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        this.plainFile = cipher.doFinal(cipherText);
        this.plainText = new String(this.plainFile, StandardCharsets.UTF_8);
    }

    public String getPlainText() {
        return plainText;
    }

    public byte[] getPlainFile() {
        return plainFile;
    }
}
