package sample;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

public class MessageDecryption {

    private String plainMessage;
    private byte[] plainFile;

    public MessageDecryption(byte[] cipherText, PrivateKey privateKeyServer) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKeyServer);
        this.plainFile = cipher.doFinal(cipherText);
        this.plainMessage = new String(this.plainFile, StandardCharsets.UTF_8);

    }

    public String getPlainMessage() {
        return plainMessage;
    }

    public byte[] getPlainFile() {
        return plainFile;
    }
}
