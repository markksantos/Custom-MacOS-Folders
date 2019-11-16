import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author markksantos.com
 */

public class ResizeImage {
	
	/**
	 *	Resize Method
	 *  inputs:
	 *    - image output path(where the image will be saved)
	 *    - image input path(the image being resized)
	 *    - scaledWidth(desired output width)
	 *    - scaledHeight(desired output Height)
	 */
  public static void resize(String inputImagePath, String outputImagePath, int scaledWidth, int scaledHeight) throws IOException {
        // reads input image
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);
 
        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,scaledHeight, inputImage.getType());
 
        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
 
        // extracts extension of output file
        String formatName = outputImagePath.substring(outputImagePath.lastIndexOf(".") + 1);
 
        // writes to output file
        ImageIO.write(outputImage, formatName, new File(outputImagePath));
  }

}