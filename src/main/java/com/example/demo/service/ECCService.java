package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * ECC 加密服務
 * 使用 Curve25519 演算法進行非對稱加密，結合 ECDH 金鑰交換和 AES-256-CBC 對稱加密
 * 
 * 主要功能：
 * 1. 產生 ECC 金鑰對
 * 2. 使用公鑰加密資料
 * 3. 使用私鑰解密資料
 * 
 * 加密流程：
 * 1. 產生臨時金鑰對
 * 2. 使用 ECDH 產生共享金鑰
 * 3. 使用 AES-256-CBC 加密資料
 * 4. 組合臨時公鑰、IV 和加密資料
 * 
 * 解密流程：
 * 1. 分離臨時公鑰、IV 和加密資料
 * 2. 使用 ECDH 重建共享金鑰
 * 3. 使用 AES-256-CBC 解密資料
 */
@Service
public class ECCService {

    /** 橢圓曲線演算法名稱 */
    private static final String ALGORITHM = "EC";
    
    /** 加密提供者名稱（BouncyCastle） */
    private static final String PROVIDER = "BC";
    
    /** 金鑰交換演算法名稱 */
    private static final String KEY_AGREEMENT_ALGORITHM = "ECDH";
    
    /** 對稱加密演算法名稱 */
    private static final String SYMMETRIC_ALGORITHM = "AES";
    
    /** 對稱加密金鑰長度（256 位元） */
    private static final int SYMMETRIC_KEY_SIZE = 256;
    
    /** 橢圓曲線點座標長度（32 bytes） */
    private static final int POINT_LENGTH = 32;

    /**
     * 初始化加密提供者
     * 註冊 BouncyCastle 作為加密提供者
     */
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 產生 ECC 金鑰對
     * 
     * @return 包含 Base64 編碼的公鑰和私鑰的字串陣列，索引 0 為公鑰，索引 1 為私鑰
     * @throws NoSuchAlgorithmException 當指定的演算法不存在時拋出
     * @throws NoSuchProviderException 當指定的加密提供者不存在時拋出
     * @throws InvalidKeySpecException 當指定的密鑰規格不存在時拋出
     */
    public String[] generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        // 初始化金鑰產生器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
        keyPairGenerator.initialize(SYMMETRIC_KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // 取得公鑰的點座標並轉換為壓縮格式
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        ECPoint point = ((ECPublicKeySpec) keyFactory.getKeySpec(keyPair.getPublic(), ECPublicKeySpec.class)).getQ();
        byte[] publicKeyBytes = point.getEncoded(true); // 使用壓縮格式
        String publicKeyBase64 = Base64.toBase64String(publicKeyBytes);

        // 取得私鑰並進行 Base64 編碼
        String privateKeyBase64 = Base64.toBase64String(keyPair.getPrivate().getEncoded());

        return new String[]{publicKeyBase64, privateKeyBase64};
    }

    /**
     * 產生 ECC 金鑰對並以 Map 格式回傳
     * 
     * @return 包含 Base64 編碼的公鑰和私鑰的 Map
     * @throws NoSuchAlgorithmException 當指定的演算法不存在時拋出
     * @throws NoSuchProviderException 當指定的加密提供者不存在時拋出
     * @throws InvalidKeySpecException 當指定的密鑰規格不存在時拋出
     */
    public Map<String, String> generateKeyPairMap() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        String[] keyPair = this.generateKeyPair();

        Map<String, String> result = new HashMap<>();
        result.put("privateKey", keyPair[1]);
        result.put("publicKey", keyPair[0]);

        return result;
    }

    /**
     * 加密字串
     * 
     * @param plaintext 要加密的文字
     * @param publicKeyBase64 Base64 編碼的公鑰
     * @return Base64 編碼的加密結果，格式為：[壓縮公鑰(33 bytes) + IV(16 bytes) + 加密資料]
     * @throws Exception 當加密過程發生錯誤時拋出
     */
    public String encrypt(String plaintext, String publicKeyBase64) throws Exception {
        // 產生臨時金鑰對
        String[] tempKeyPair = generateKeyPair();
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        
        // 重建公鑰
        byte[] publicKeyBytes = Base64.decode(publicKeyBase64);
        PrivateKey tempPrivateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(tempKeyPair[1])));
        ECPoint point = keyFactory.getKeySpec(tempPrivateKey, ECPrivateKeySpec.class).getParams().getCurve().decodePoint(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(new ECPublicKeySpec(point, keyFactory.getKeySpec(tempPrivateKey, ECPrivateKeySpec.class).getParams()));
        
        // 建立金鑰協議並產生共享金鑰
        KeyAgreement keyAgreement = KeyAgreement.getInstance(KEY_AGREEMENT_ALGORITHM, PROVIDER);
        keyAgreement.init(tempPrivateKey);
        keyAgreement.doPhase(publicKey, true);
        byte[] sharedSecret = keyAgreement.generateSecret();
        SecretKey secretKey = new SecretKeySpec(sharedSecret, 0, POINT_LENGTH, SYMMETRIC_ALGORITHM);
        
        // 產生隨機 IV
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        // 使用 AES/CBC/PKCS5Padding 加密
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedData = cipher.doFinal(plaintext.getBytes());
        
        // 組合臨時公鑰、IV 和加密資料
        byte[] tempPublicKey = Base64.decode(tempKeyPair[0]);
        byte[] combined = new byte[tempPublicKey.length + iv.length + encryptedData.length];
        System.arraycopy(tempPublicKey, 0, combined, 0, tempPublicKey.length);
        System.arraycopy(iv, 0, combined, tempPublicKey.length, iv.length);
        System.arraycopy(encryptedData, 0, combined, tempPublicKey.length + iv.length, encryptedData.length);
        
        return Base64.toBase64String(combined);
    }

    /**
     * 解密字串
     * 
     * @param encryptedData Base64 編碼的加密資料，格式為：[壓縮公鑰(33 bytes) + IV(16 bytes) + 加密資料]
     * @param privateKeyBase64 Base64 編碼的私鑰
     * @return 解密後的文字
     * @throws Exception 當解密過程發生錯誤時拋出
     */
    public String decrypt(String encryptedData, String privateKeyBase64) throws Exception {
        // Base64 解碼加密資料
        byte[] combined = Base64.decode(encryptedData);
        
        // 分離臨時公鑰、IV 和加密資料
        byte[] tempPublicKeyBytes = Arrays.copyOfRange(combined, 0, 33); // 壓縮格式的公鑰長度
        byte[] iv = Arrays.copyOfRange(combined, 33, 33 + 16);
        byte[] encryptedBytes = Arrays.copyOfRange(combined, 33 + 16, combined.length);
        
        // 重建臨時公鑰
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(privateKeyBase64)));
        ECPoint point = keyFactory.getKeySpec(privateKey, ECPrivateKeySpec.class).getParams().getCurve().decodePoint(tempPublicKeyBytes);
        PublicKey tempPublicKey = keyFactory.generatePublic(new ECPublicKeySpec(point, keyFactory.getKeySpec(privateKey, ECPrivateKeySpec.class).getParams()));
        
        // 建立金鑰協議並重建共享金鑰
        KeyAgreement keyAgreement = KeyAgreement.getInstance(KEY_AGREEMENT_ALGORITHM, PROVIDER);
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(tempPublicKey, true);
        byte[] sharedSecret = keyAgreement.generateSecret();
        SecretKey secretKey = new SecretKeySpec(sharedSecret, 0, POINT_LENGTH, SYMMETRIC_ALGORITHM);
        
        // 使用 AES/CBC/PKCS5Padding 解密
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        
        return new String(decryptedBytes);
    }
} 