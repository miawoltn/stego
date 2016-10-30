/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steganography.lib;

import java.awt.image.BufferedImage;

/**
 *
 * @author Muhammad Amin
 */
public class Embeding {
 
    /**
     *
     * @param cipher
     * @param coverImage
     * @return
     * @throws Exception
     */
    public static BufferedImage Embed(String cipher, BufferedImage coverImage) throws Exception{
        int[] redAndBlueComponents = Operation.getRedAndBlueComponent(coverImage);
        int[] greenComponent = Operation.WriteMessageLength(coverImage, cipher.length());
        byte[] messagebyte = Operation.ConvertMessageToByte(cipher);  //cipher.getBytes();
        if(messagebyte.length > redAndBlueComponents.length) throw new Exception("Message is too long!");
        int Ki = quotientOfCipherAndEight(cipher.length());
        int Ai = SumOfKiDigits(Ki);
        int Mi = AiModuloEight(Ai);
        for(int i = 0; i < messagebyte.length; i++){
            int add = messagebyte[i];
            for(int bit=7; bit>=0; --bit){
                //System.out.print(redAndBlueComponents[Ki]+" ");
                int currentBit = (add >>> bit) & 1;
                if(currentBit == 1) {
                    redAndBlueComponents[Ki] = Operation.SetBit(redAndBlueComponents[Ki], Mi);
                } else {
                    redAndBlueComponents[Ki] = Operation.UnSetBit(redAndBlueComponents[Ki], Mi);
                }
                
                Ki += Mi;
                Ai = SumOfKiDigits(Ki);
                Mi = AiModuloEight(Ai);
            } 
        }
        System.out.println(redAndBlueComponents[redAndBlueComponents.length - 1]);
        redAndBlueComponents[redAndBlueComponents.length - 1] = messagebyte.length;
        System.out.println(redAndBlueComponents[redAndBlueComponents.length - 1]);
        coverImage = Operation.WriteCoverImage(coverImage, redAndBlueComponents,greenComponent);
        System.out.println(redAndBlueComponents[redAndBlueComponents.length - 1]);
        return coverImage;
    }

    public static String Extract(BufferedImage coverImage){
        int[] redAndBlueComponents = Operation.getRedAndBlueComponent(coverImage);
        int messageSize = getMessageLength(coverImage);// redAndBlueComponents[redAndBlueComponents.length - 1];
        //System.out.println(messageSize); //debugging statement
        byte[] secretMessage = new byte[messageSize];
        int Ki = quotientOfCipherAndEight(messageSize);
        int Ai = SumOfKiDigits(Ki);
        int Mi = AiModuloEight(Ai);
        for(int i = 0; i < messageSize; i++)
        {      
            for(int j=0; j<8; ++j)
            {
              int position = getBitPosition(Mi);
              //System.out.println(redAndBlueComponents[Ki] + " ");
              secretMessage[i] = (byte)((secretMessage[i] << 1) | (redAndBlueComponents[Ki] >> position) & 1);
              Ki += Mi;
              Ai = SumOfKiDigits(Ki);
              Mi = AiModuloEight(Ai);
            }	            
            //System.out.println();
        }
        //System.out.println("Secret msg size: "+ secretMessage.length);
        return new String(secretMessage);
    }
    private static int quotientOfCipherAndEight(int length) {
        return length/8;
    }

    public static int SumOfKiDigits(int Ki) {
       String[] digits = Integer.toString(Ki).split("(?<=.)");
       int sum = 0;
       for(String str : digits)
           sum += Integer.parseInt(str);
       
       return sum;
    }

    private static int AiModuloEight(int Ai) {
        int remainder =  Ai%8;
        return remainder == 0 ? 7:remainder;
    }

    private static int getBitPosition(int Mi) {
        switch(Mi)
        {
           case 0:
           case 7:
           case 6:
           case 5:
           case 4:
                return 0;
           default:
               return Mi;
        }
    }
    
    private static int getMessageLength(BufferedImage coverImage)
    {
        int length = 0;
         int[] greenPixels = Operation.getGreenComponent(coverImage,0);
         for(int i=0; i<32; ++i)
        {
           length = (length << 1) | (greenPixels[i] & 1);
        }
        return length;
    }

    private Embeding() {
    }
}
