package com.fadlan;

import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

// THIS IS AN ONGOING PROGRAM, WILL BE CONTNUED WHEN NECESSARY.
// FOR NOW WE WILL USE PasswordEncrypt.java to handle password encryption.

public class AESPasswordCrypt {
    private static SecretKeySpec secretKey;
    private static byte[] key;

    public static void setKey(){
        try {
            // key = myKey.getBytes("UTF-8");

        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    private String decrypt(String nonplain){
        return "";
    }
    private String encrypt(String plain){
        return "";
    }
}
