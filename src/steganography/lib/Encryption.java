/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steganography.lib;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Muhammad Amin
 */
public class Encryption {
    
    /**
     *
     * 
     * @param message
     * @param coverImage
     * @return EncryptedMessage
     */
    public static String Encrypt(String message, BufferedImage coverImage)
    {
        byte[] messageByte = Operation.ConvertMessageToByte(message);
        int[] greenPixels = Operation.getGreenComponent(coverImage,messageByte.length);
        byte[] cipherByte = new byte[messageByte.length];
        System.out.println("Sizes:");
         System.out.println(messageByte.length); 
            System.out.println(greenPixels.length);
        for(int i = 0; i < messageByte.length; i++)
        {
            /*if(greenPixels[i] > 127) {
                continue;
            }*/
           
           // System.out.println(messageByte[i]);
            cipherByte[i] = (byte)(messageByte[i] ^ greenPixels[i % greenPixels.length]);             
        }              
       return new String(cipherByte);
    }
    
        
    public static String Encrypt3(String message, String password)
    {
        byte[] messageByte = Operation.ConvertMessageToByte(message);
        byte[] passwordByte = Operation.ConvertMessageToByte(password);
        byte[] cipherByte = new byte[messageByte.length];
        System.out.println("Sizes:");
         System.out.println(messageByte.length); 
            System.out.println(passwordByte.length);
            
        for(int i = 0; i < messageByte.length; i++)
        {
            cipherByte[i] = (byte)(messageByte[i] ^ passwordByte[i % passwordByte.length]);             
        }    
            
        for(int i = 0; i < messageByte.length; i++)
        {
            /*if(greenPixels[i] > 127) {
                continue;
            }*/
           
           // System.out.println(messageByte[i]);
            cipherByte[i] = (byte)(cipherByte[i] & passwordByte[i % passwordByte.length]);             
        }              
       return new String(cipherByte);
    }
    
    public static String Encrypt1(String message, String pass) throws Exception
    {
        String password = pass;
        SecretKeySpec key = new SecretKeySpec(Operation.padPassword(password), "AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(message.getBytes(StandardCharsets.UTF_8));
        Encoder encoder = Base64.getEncoder();
        String encrypted=encoder.encodeToString(encVal);
        System.out.println("Encrypted size: "+ encrypted.getBytes().length);
        encoder.encodeToString(encVal);
        return encrypted;
    }
    
    public static String Decrypt(String message, String pass) throws Exception
    {
        String password = pass;
        Cipher c;
        String decryptedValue = null;
        try {
            c = Cipher.getInstance("AES");
            SecretKeySpec key = new SecretKeySpec(Operation.padPassword(password), "AES");
            c.init(Cipher.DECRYPT_MODE, key);
            Decoder decoder = Base64.getDecoder();
            byte[] decordedValue = decoder.decode(message.getBytes(StandardCharsets.UTF_8));
            byte[] decValue;
            decValue = c.doFinal(decordedValue);
            decryptedValue = new String(decValue);
      }
        catch (Exception e) {e.printStackTrace();
        throw new Exception("Wrong Passoword");
        }
        return decryptedValue;
            }

    private Encryption() {
    }
   
}
