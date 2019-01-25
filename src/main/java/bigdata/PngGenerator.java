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

public class PngGenerator implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final int HEIGHT = 1201;
	static final int WIDTH = 1201;
	static final int GRADIENT_LENGHT = 25000;
	
	private ShortBuffer sb = null;
	private BufferedImage img = null;
	
	public PngGenerator(ShortBuffer sb) {
			this.sb = sb;
	}
	
	public PngGenerator() {
	}

	public void setSb(ShortBuffer sb) {
		this.sb = sb;
	}
	
	public void generateEmtyImageWithColor(Color color) {
		img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D graphics = img.createGraphics();
		
		graphics.setColor (color);
		graphics.fillRect ( 0, 0, img.getWidth(), img.getHeight());
	
	}
		
	public void generateWithImageGradient(String imagePath) {
		
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

				int fila = x / HEIGHT;
				int columna = x % HEIGHT;

				int fila_grad = (10000 + sb.get(x)) / 200;
				int colum_grad = (10000 + sb.get(x)) % 200;

				Color mycolor;
				if (sb.get(x) < -10000 || sb.get(x) > 15000) 
					mycolor = Color.RED;
				else 
					mycolor = new Color(image.getRGB(colum_grad, fila_grad));

				img.setRGB(columna, fila, mycolor.getRGB());

			}
			    	
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public void generateWithDefaults() {
	
	}
	
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
	
	public void writePng(String name) {
		try {
			ImageIO.write(img, "png", new File(name+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
