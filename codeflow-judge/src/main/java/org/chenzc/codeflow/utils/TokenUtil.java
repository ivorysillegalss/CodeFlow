package org.chenzc.codeflow.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TokenUtil {
    public static String generateToken(String judgeServerToken) throws NoSuchAlgorithmException {
        // 使用 SHA-256 算法进行哈希
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(judgeServerToken.getBytes());

        // 将字节数组转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }

        // 返回生成的 SHA-256 哈希值
        return hexString.toString();
    }
}
