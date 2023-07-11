package com.jinloongd.samples.util.crypto;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;

/**
 * @author JinLoong.Du
 */
public class RSAUtils {

    public static final String ALGORITHM_RSA = "RSA";

    public static final int DEFAULT_KEY_SIZE = 2048;

    static {
        // add BouncyCastle provider
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String TYPE_PUBLIC_KEY = "PUBLIC KEY";

    private static final String TYPE_PRIVATE_KEY = "PRIVATE KEY";

    /**
     * 生成默认长度(2048 bits)的 RSA 公钥私钥对
     * @return {@link KeyPair}
     */
    public static KeyPair generateKeyPair() {
        return generateKeyPair(DEFAULT_KEY_SIZE);
    }

    /**
     * 生成 RSA 公钥私钥对
     * @param keySize 密钥模(modules) 长度(bit)
     * @return {@link KeyPair}
     */
    public static KeyPair generateKeyPair(int keySize) {
        return generateKeyPair(keySize, (byte[]) null);
    }

    /**
     * 生成 RSA 公钥私钥对
     * @param keySize 密钥模(modules) 长度(bit)
     * @param seed 强随机数生成器对象{@link SecureRandom} 种子
     * @return {@link KeyPair}
     */
    public static KeyPair generateKeyPair(int keySize, byte[] seed) {
       return generateKeyPair(keySize, (seed == null) ? new SecureRandom() : new SecureRandom(seed));
    }

    /**
     * 生成 RSA 公钥私钥对
     * @param keySize 密钥模(modules) 长度(bit)
     * @param random 强随机数生成器对象{@link SecureRandom}
     * @return {@link KeyPair}
     */
    public static KeyPair generateKeyPair(int keySize, SecureRandom random) {
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        } catch (NoSuchAlgorithmException e) {
            // ignore
        }
        keyPairGen.initialize(keySize, random);
        return keyPairGen.generateKeyPair();
    }

    public static byte[] encryptByPublicKey(byte[] data, PublicKey key) throws GeneralSecurityException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM_RSA);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            // ignore
        }
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] encryptByPrivateKey(byte[] data, PrivateKey key) throws GeneralSecurityException{
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM_RSA);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            // ignore
        }
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] decryptByPublicKey(byte[] data, PublicKey key) throws GeneralSecurityException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM_RSA);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            // ignore
        }
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] decryptByPrivateKey(byte[] data, PrivateKey key) throws GeneralSecurityException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM_RSA);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            // ignore
        }
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static void writeAsPEMFormat(PublicKey publicKey, Writer out) throws IOException {
        writeAsPEMFormat(TYPE_PUBLIC_KEY, publicKey.getEncoded(), out);
    }

    public static void writeAsPEMFormat(PrivateKey privateKey, Writer out) throws IOException {
        writeAsPEMFormat(TYPE_PRIVATE_KEY, privateKey.getEncoded(), out);
    }

    private static void writeAsPEMFormat(String type, byte[] key, Writer out) throws IOException {
        PemObject pemObject = new PemObject(type, key);
        PemWriter pemWriter = new PemWriter(out);
        pemWriter.writeObject(pemObject);
        pemWriter.close();
    }

    public static Key loadAsPEMString(String pemString) throws IOException {
        if (pemString == null) {
            throw new IllegalArgumentException("PEM string must not be null.");
        }

        StringReader stringReader = new StringReader(pemString);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);

        if (pemString.contains(TYPE_PUBLIC_KEY)) {
            PEMParser pemParser = new PEMParser(stringReader);
            return converter.getPublicKey((SubjectPublicKeyInfo) pemParser.readObject());
        } else if (pemString.contains(TYPE_PRIVATE_KEY)) {
            PEMParser pemParser = new PEMParser(stringReader);
            return converter.getPrivateKey((PrivateKeyInfo) pemParser.readObject());
        } else {
            throw new IllegalArgumentException("Invalid PEM format content");
        }
    }
}
