/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steganography.lib;



import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class Histogram {
    BufferedImage image;
    int[] RedFrequency;
    int[] GreenFrequency;
    int[] BlueFrequency;
    int redColor;
    int greenColor;
    int blueColor;
    int rgb;
    long sumOfRedFrequency;
    long sumOfBlueFrequency;
    long sumOfGreenFrequency;
    int w;
    int h;
    public Histogram(BufferedImage image,int h, int w){
        this.h = h;
        this.w = w;
        this.image = image;
    }
    public void paint(Graphics g) {
        Graphics2D g2D=(Graphics2D)g;
        RedFrequency=new int[256];
        GreenFrequency=new int[256];
        BlueFrequency=new int[256];
        int width = image.getWidth();
        int height = image.getHeight();
        for(int i=0;i<width;i++) {
            for(int j=0;j<height;j++) {
                
                rgb =image.getRGB(i,j);
                redColor = (rgb >> 16) & 0xff;
                greenColor = (rgb >> 8) & 0xff;
                blueColor = (rgb) & 0xff;
                RedFrequency[redColor]++;
                GreenFrequency[greenColor]++;
                BlueFrequency[blueColor]++;
            }
        }
        
        int iw=w/2;
        int ih=h/2;
       
       
      
       for(int i=0;i<256;i++) {
            
            g2D.setColor(Color.RED);
    g2D.drawLine(20+i,h-20,20+i,h-(RedFrequency[i]/10)-20 );
             
             g2D.setColor(Color.GREEN);
            g2D.drawLine(20+i,h-20,20+i,h-(GreenFrequency[i]/10)-20);
            g2D.setColor(Color.BLUE);
            g2D.drawLine(20+i,h-20,20+i,h-(BlueFrequency[i]/10)-20);
            
            sumOfRedFrequency+=RedFrequency[i]*i;
            sumOfGreenFrequency+=GreenFrequency[i]*i;
            sumOfBlueFrequency+=BlueFrequency[i]*i;
            
        } 
         g.setColor(Color.BLACK);
        g2D.drawLine(10,h-10,10,h-256);
        g2D.drawLine(10,h-10,256,h-10);
        g2D.drawString("0-->255",260,h-10);
        g2D.setFont(new Font("Aril",Font.BOLD,12));
        
        g2D.drawString("RGB Histogram",300,100);
        g2D.drawString("Red Mean :",300,120);
        g2D.drawString(Long.toString( sumOfRedFrequency/(height*width)),400,120);
        g2D.drawString("Green Mean :",300,140);
        g2D.drawString(Long.toString(( sumOfGreenFrequency/(height*width))),400,140);
        g2D.drawString("Blue Mean :",300,160);
        g2D.drawString(Long.toString(( sumOfBlueFrequency/(height*width))),400,160);       
    }
    
   
}
