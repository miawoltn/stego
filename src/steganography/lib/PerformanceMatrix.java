/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steganography.lib;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

/**
 *
 * @author Muhammad Amin
 */
public class PerformanceMatrix {
    private final BufferedImage coverImage;
    private final BufferedImage stegoImage;
    private double mse;
    private double psnr;
    public PerformanceMatrix(BufferedImage coverImage, BufferedImage stegoImage){
        this.coverImage = coverImage;
        this.stegoImage = stegoImage;
    }
    
   public double CalculateMSE(){
       //double mse = 0;
        int width = coverImage.getWidth();
        int height = coverImage.getHeight();
        Raster r1 = coverImage.getRaster();
        Raster r2 = stegoImage.getRaster();
        for (int j = 0; j < height; j++)
                for (int i = 0; i < width; i++)
                        mse += Math.pow(r1.getSample(i, j, 0) - r2.getSample(i, j, 0), 2);
        mse /= (double) (width * height);
       return  mse;
   }
   
   public double calculatePSNR(){
       int maxVal = 255;
        double x = Math.pow(maxVal, 2) / mse;
        double psnr = 10.0 * logbase10(x);
        //System.err.println("PSNR = " + psnr);
        return psnr;
   }
   
   public static double printPSNR(BufferedImage im1, BufferedImage im2) {
		assert(
			im1.getType() == im2.getType()
				&& im1.getHeight() == im2.getHeight()
				&& im1.getWidth() == im2.getWidth());

		double mse = 0;
		int width = im1.getWidth();
		int height = im1.getHeight();
		Raster r1 = im1.getRaster();
		Raster r2 = im2.getRaster();
		for (int j = 0; j < height; j++)
			for (int i = 0; i < width; i++)
				mse += Math.pow(r1.getSample(i, j, 0) - r2.getSample(i, j, 0), 2);
		mse /= (double) (width * height);
		System.err.println("MSE = " + mse);
		int maxVal = 255;
		double x = Math.pow(maxVal, 2) / mse;
		double psnr = 10.0 * logbase10(x);
		System.err.println("PSNR = " + psnr);
		return psnr;
	}
        public static double logbase10(double x) {
		return Math.log(x) / Math.log(10);
	}
}
