package io.choerodon.hap.security.crypto.encrypt;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * @author shiliyan
 */
class CipherUtils {
    public static SecretKey newSecretKey(String algorithm, String password) {
        return newSecretKey(algorithm, new PBEKeySpec(password.toCharArray()));
    }

    public static SecretKey newSecretKey(String algorithm, PBEKeySpec keySpec) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
            return factory.generateSecret(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Not a valid encryption algorithm", e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("Not a valid secret key", e);
        }
    }

    public static Cipher newCipher(String algorithm) {
        try {
            return Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Not a valid encryption algorithm", e);
        } catch (NoSuchPaddingException e) {
            throw new IllegalStateException("Should not happen", e);
        }
    }

    public static <T extends AlgorithmParameterSpec> T getParameterSpec(Cipher cipher, Class<T> parameterSpecClass) {
        try {
            return cipher.getParameters().getParameterSpec(parameterSpecClass);
        } catch (InvalidParameterSpecException e) {
            throw new IllegalArgumentException("Unable to access parameter", e);
        }
    }

    public static void initCipher(Cipher cipher, int mode, SecretKey secretKey) {
        initCipher(cipher, mode, secretKey, null);
    }

    public static void initCipher(Cipher cipher, int mode, SecretKey secretKey, byte[] salt, int iterationCount) {
        initCipher(cipher, mode, secretKey, new PBEParameterSpec(salt, iterationCount));
    }

    public static void initCipher(Cipher cipher, int mode, SecretKey secretKey, AlgorithmParameterSpec parameterSpec) {
        try {
            if (parameterSpec != null) {
                cipher.init(mode, secretKey, parameterSpec);
            } else {
                cipher.init(mode, secretKey);
            }
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("Unable to initialize due to invalid secret key", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new IllegalStateException("Unable to initialize due to invalid decryption parameter spec", e);
        }
    }

    public static byte[] doFinal(Cipher cipher, byte[] input) {
        try {
            return cipher.doFinal(input);
        } catch (IllegalBlockSizeException e) {
            throw new IllegalStateException("Unable to invoke Cipher due to illegal block size", e);
        } catch (BadPaddingException e) {
            throw new IllegalStateException("Unable to invoke Cipher due to bad padding", e);
        }
    }
}