package com.appartika.easyweb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Extensions {
    public static String join(String[] array) {
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s);
        }
        return sb.toString();
    }
    public static boolean IOexcept(IOException ioe, HttpExchange exc) {
        System.out.println(ioe);
        PrintWriter outStreamObject = new PrintWriter(exc.getResponseBody());
        outStreamObject.println(ioe.toString());
        outStreamObject.close();
        exc.close();
        return false;
    }
    /**
     * Parse a JSON string to Map, using a GSON lib
     * @param body JSON string
     */
    public static Map fromJson(String body) {
        Map<String,Object> map = new HashMap<>();
        Gson gson = new Gson();
        try {
            map = (Map<String, Object>) gson.fromJson(body, map.getClass());
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            return map;
        }
    }
    /**
     * Parse a Map to JSON string, using a GSON lib
     * @param body Map dictionary
     */
    public static String toJson(Map body) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(body);
    }
    /** ------------------------- Encryption methods ----------------------------- */
    /**
     * Encrypt a string in md5 hash, next - concatenate hash with salt and encrypt it via sha1
     * @param str string for encryption
     * @param salt salt
     * @return encryption string
     */
    public static String encrypt(String str, String salt) {
        return sha1(MD5(str) + salt);
    }
    /**
     * Encrypt string via sha1
     * @param param string for encryption
     * @return sha1 hash
     */
    public static String sha1(String param) {
        try {
            MessageDigest SHA = MessageDigest.getInstance("SHA-1");
            SHA.reset();
            SHA.update(param.getBytes("UTF-8"), 0, param.length());
            byte[] sha1hash = SHA.digest();
            return bytesToHexStr(sha1hash);
        } catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
            System.out.println(e);
            return "null";
        }
    }
    /**
     * Encrypt string via md5
     * @param md5 string for encryption
     * @return md5 hash
     */
    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            return "null";
        }
    }
    /** Parse a byte massive in hex-string */
    private static String bytesToHexStr(byte[] raw) {
        char[] kDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        int length = raw.length;
        char[] hex = new char[length * 2];
        for (int i = 0; i < length; i++) {
            int value = (raw[i] + 256) % 256;
            int highIndex = value >> 4;
            int lowIndex = value & 0x0f;
            hex[i * 2 + 0] = kDigits[highIndex];
            hex[i * 2 + 1] = kDigits[lowIndex];
        }
        return new String(hex);
    }
}
