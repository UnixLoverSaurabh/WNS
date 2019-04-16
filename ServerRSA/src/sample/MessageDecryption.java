package sample;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class MessageDecryption {

    private String plainMessage;
    private byte[] plainFile;

    public MessageDecryption(byte[] cipherText, PrivateKey privateKeyServer, byte[] signature, PublicKey publicKeyClient) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, SignatureException {

        Signature sig = Signature.getInstance("MD5WithRSA");
        sig.initVerify(publicKeyClient);
        sig.update(cipherText);
        if (sig.verify(signature)) {
            // Now decrypt the message
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKeyServer);
            this.plainFile = cipher.doFinal(cipherText);
            this.plainMessage = new String(this.plainFile, StandardCharsets.UTF_8);
        }
        else {
             System.out.println("Signature failed");
        }

    }

    public String getPlainMessage() {
        return plainMessage;
    }

    public byte[] getPlainFile() {
        return plainFile;
    }
}
