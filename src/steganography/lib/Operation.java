/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steganography.lib;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author Muhammad Amin
 */
public class Operation {
    
    public static byte[] ConvertMessageToByte(String message){
        return message.getBytes();
    }
    
   public static int[] getGreenComponent(BufferedImage coverImage, int size){
       int imageHeight = getImageHeight(coverImage);
       int imageWidth = getImageWidth(coverImage);
       int[] greenPixels = new int[imageHeight*imageWidth];
       int counter = 0;
       for(int row = 0; row < imageHeight; row++){
           for(int col = 0; col < imageWidth; col++){
               greenPixels[counter++] = getGreenValue(coverImage.getRGB(col, row));                
           }
       }
     //  greenPixels = Operation.ConvertToArray(arrayList);
       return greenPixels;
   }
   
   public static int[] getRedAndBlueComponent(BufferedImage coverImage){
       int imageHeight = getImageHeight(coverImage);
       int imageWidth = getImageWidth(coverImage);
       List<Integer> redAndBluePixels = new ArrayList<>();
       for(int row = 0; row < imageHeight; row++){
           for(int col = 0; col < imageWidth; col++){
               int[] RB = getRedAndBlueValue(coverImage.getRGB(col, row)); 
               redAndBluePixels.add(RB[0]);
               redAndBluePixels.add(RB[1]);               
           }
       }
       
       return ConvertToArray(redAndBluePixels);
   }
   
   public static int getImageHeight(BufferedImage coverImage){
       return coverImage.getHeight();
   }

    private static int getImageWidth(BufferedImage coverImage) {
        return coverImage.getWidth();
    }

    private static int getGreenValue(int rgb) {
        return (rgb & 0x0000FF00) >>> 8;
    }

    private static boolean hasAlpha(BufferedImage coverImage){
        return coverImage.getColorModel().hasAlpha();
    }
    private static int getAlphaValue(int rgb){
         return (rgb & 0xFF000000) >>> 24;
    }
    private static int[] getRedAndBlueValue(int rgb) {
	int red = ((rgb & 0x00FF0000) >>> 16);  
	int blue = ((rgb & 0x000000FF)	   );
	return(new int[]{red,blue});
    }

    private static byte[] pixelComponent(int rgb)
    {
        byte alpha = (byte)((rgb & 0xFF000000) >>> 24); //0
        byte red = (byte)((rgb & 0x00FF0000) >>> 16); //0
        byte green = (byte)((rgb & 0x0000FF00) >>> 8 ); //0
        byte blue = (byte)((rgb & 0x000000FF) );
        return(new byte[]{alpha,red,green,blue});
    }
    private static int getPixel(int alpha, int red, int green, int blue){
        int pixel;
        pixel = alpha == 0? 0:(alpha & 0xff) << 24;
        pixel += (red & 0xff) << 16;         
        pixel += (green & 0xff) << 8;
        pixel += (blue & 0xff); 
        return pixel;
    }
    private static int[] ConvertToArray(List<Integer> redAndBluePixels) {
       int[] redAndBlueArray = new int[redAndBluePixels.size()];
       for(int i = 0; i < redAndBlueArray.length; i++)
       {
           redAndBlueArray[i] = redAndBluePixels.get(i);
       }
       
       return redAndBlueArray;
    }

    public static int SetBit(int redAndBlueComponent, int Mi) {
       switch(Mi)
       {
           case 0:
           case 7:
           case 6:
           case 5:
           case 4:
               redAndBlueComponent |= 0b1;
               break;
           default:
               redAndBlueComponent |= (1 << Mi);
       }
       
       return redAndBlueComponent;
    }

    public static int UnSetBit(int redAndBlueComponent, int Mi) {
        switch(Mi)
       {
           case 0:
           case 7:
           case 6:
           case 5:
           case 4:
               redAndBlueComponent &= ~0b1;
               break;
           default:
               redAndBlueComponent &= ~(1 << Mi);
       }
       
       return redAndBlueComponent;
    }

    static BufferedImage WriteCoverImage(BufferedImage coverImage, int[] redAndBlueComponents, int[] greenComponent) {
       int counter = 0;
       int gcounter = 0;
       int pixel;
       int alpha;
       int green; 
       int red;
       int blue;
       int imageHeight = getImageHeight(coverImage);
       int imageWidth = getImageWidth(coverImage);
        for(int row = 0; row < imageHeight; row++){
           for(int col = 0; col < imageWidth; col++){
                 pixel = coverImage.getRGB(col, row);
                 alpha = hasAlpha(coverImage)? getAlphaValue(pixel):0; //(pixel & 0xFF000000) >>> 24;
                 green = greenComponent[gcounter++];//getGreenValue(pixel);// (pixel & 0x0000FF00) >>> 8;
                 red =  redAndBlueComponents[counter++];
                 blue = redAndBlueComponents[counter++];
                 pixel = getPixel(alpha,red,green,blue);
                 coverImage.setRGB(col, row, pixel);
           }
        }
        
        return coverImage;
    }

    public static int[] WriteMessageLength(BufferedImage coverImage, int size)
    {
        int[] greenPixels = Operation.getGreenComponent(coverImage,size);
        byte length[] = pixelComponent(size);
        int offset = 0;
        for(int i=0; i<length.length; ++i)
        {
            int add = length[i];
            for(int bit=7; bit>=0; --bit, ++offset)
            {
               int b = (add >>> bit) & 1;
               greenPixels[offset] = (byte)((greenPixels[offset] & 0xFE) | b );
            }
        }
        return greenPixels;
    }
    public static BufferedImage LoadImage(BufferedImage image) throws IOException {
        BufferedImage new_image  = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
	Graphics2D graphics   = new_image.createGraphics();
	graphics.drawRenderedImage(image, null);
	graphics.dispose(); 
	return new_image;
    }
    
    public static byte[] padPassword(String password){
        int keyLength = 128;
        byte[] keyBytes = new byte[keyLength/8];
        
        Arrays.fill(keyBytes, (byte) 0x0);
       
        byte[] passwordBytes;
        try {
            passwordBytes = password.getBytes("UTF-8");
            int length = passwordBytes.length < keyBytes.length ? passwordBytes.length : keyBytes.length;
            System.arraycopy(passwordBytes, 0, keyBytes, 0, length);
        } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
            }
        return keyBytes;
        }

    public static boolean isSupported(BufferedImage image){
        System.out.println(image.getType());
        return image.getType() == BufferedImage.TYPE_3BYTE_BGR;
    }
    private Operation() {
    }
}
