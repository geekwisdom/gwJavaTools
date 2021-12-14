package org.geekwisdom;

public interface GWCryptProvider {

    int get_iv_length();
    String MakeSecretKey(String startKey);
    byte[] Encrypt(String message_in, byte[] key, byte[] iv);
    String Decrypt(byte [] ciphertext, byte[] key, byte[] iv);
}
