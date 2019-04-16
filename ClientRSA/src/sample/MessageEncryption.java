package sample;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class MessageEncryption {

    private byte[] cipherText;
    private byte[] sign;

    public MessageEncryption(String message, PublicKey publicKeyServer, PrivateKey privateKeyClient) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, SignatureException {
        byte[] plainText = message.getBytes(StandardCharsets.UTF_8);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKeyServer);
        this.cipherText = cipher.doFinal(plainText);

        Signature signature = Signature.getInstance("MD5WithRSA");
        signature.initSign(privateKeyClient);
        signature.update(cipherText);
        this.sign  = signature.sign();
    }

    public MessageEncryption(byte[] plainText, PublicKey publicKeyServer, PrivateKey privateKeyClient) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, SignatureException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKeyServer);
        this.cipherText = cipher.doFinal(plainText);

        Signature signature = Signature.getInstance("MD5WithRSA");
        signature.initSign(privateKeyClient);
        signature.update(cipherText);
        this.sign  = signature.sign();
    }

    public byte[] getCipherText() {
        return cipherText;
    }

    public byte[] getSignature() {
        return sign;
    }
}
