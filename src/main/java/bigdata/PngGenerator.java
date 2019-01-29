package bigdata;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ShortBuffer;

import javax.imageio.ImageIO;

import scala.Serializable;

/**
 * PngGenerator contains all the functions needed to convert, generate and create PNG 
 */

public class PngGenerator implements Serializable {

	
	private static final long serialVersionUID = 1L;
	static final int HEIGHT = 1201;
	static final int WIDTH = 1201;
	static final int GRADIENT_LENGHT = 25000;
	
	private ShortBuffer sb = null;
	private BufferedImage img = null;
	
	/**
	 * Constructor for the PngGenerator Class with a short buffer
	 * @param sb ShortBuffer
	 */
	public PngGenerator(ShortBuffer sb) {
			this.sb = sb;
	}
	
	public PngGenerator() {
	}

	
	public void flushBuffer() {
		img.flush();
	}
	
	public void setSb(ShortBuffer sb) {
		this.sb = sb;
	}
	
	/**
	 * Generates an image filled with a color
	 * @param color The color we want to fill our image with
	 * @return void
	 */

	public void generateEmptyImageWithColor(Color color) {
		img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D graphics = img.createGraphics();
		
		graphics.setColor (color);
		graphics.fillRect ( 0, 0, img.getWidth(), img.getHeight());
	
	}
		
	/**
	 * Generates an image filled with the gradient for the height
	 * @param imagePath Path to a gradient PNG used
	 * @return void
	 */
	public void generateImageWithGradient(String imagePath) {
		
		URL url =  ProjetMaps.class.getResource(imagePath); 
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(url);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			
			img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

			for (int x = 0; x < sb.limit(); x++) {

				int row = x / HEIGHT;
				int column = x % HEIGHT;

				int offset = (10000 + sb.get(x));
				
				int row_grad = offset / 200;
				int colum_grad = offset % 200;

				Color pixelcolor;
				if (sb.get(x) < -10000 || sb.get(x) > 15000) 
					pixelcolor = Color.RED;
				else 
					pixelcolor = new Color(image.getRGB(colum_grad, row_grad));

				img.setRGB(column, row, pixelcolor.getRGB());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		image.flush();

	}
	
	
	public void generateWithDefaults() {
	
	}
	
	/**
	 * Transcript a PNG file to an Array of byte
	 * @return byte[]
	 */
	public byte[] getBytes() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			
			ImageIO.write(img, "png", baos );
		
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		
		return imageInByte;
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Save our generated image in a PNG file
	 * @param name Name we want for our file
	 * @return void
	 */
	public void writePng(String name) {
		try {
			ImageIO.write(img, "png", new File(name+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
