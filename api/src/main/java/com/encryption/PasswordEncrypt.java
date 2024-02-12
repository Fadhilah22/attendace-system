package com.encryption;

public class PasswordEncrypt {
    public String encrypt(String plain){
        String encryptedText = "";
        // encryption formula here
        for(int i=0; i<plain.length(); i++){
            encryptedText += "" + (char)(10 + (int)plain.toCharArray()[i]);
        }
        return encryptedText;
    }
    public String decrypt(String unplain){
        String decryptedText = "";
        // decryption formula here
        for(int i=0; i<unplain.length(); i++){
            decryptedText += "" + (char)((int)unplain.toCharArray()[i] - 10);
        }
        return decryptedText;
    }

    public void test(){
        System.out.println("plain     : Degabalagodai");
        System.out.println("encrypted : " + encrypt("Degabalagodai"));
        System.out.println("decrypted : " + decrypt("" + encrypt("Degabalagodai")));
    }

    public static void main(String[] args) {
        PasswordEncrypt run = new PasswordEncrypt();
        run.test();
    }
}
