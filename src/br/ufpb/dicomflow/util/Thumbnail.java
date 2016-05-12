package  br.ufpb.dicomflow.util;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
/**
 * Classe que produz thumbnails de imagens.
 */
public class Thumbnail {

	/**
	 * Método que produz thumbnails com dimensão e qualidade específicas, a partir do array de bytes de uma imagem qualquer, .
	 * @param inFile o array de bytes da imagem
	 * @param newWidth a largura do thumbnail
	 * @param newHeight a altura do thumbnail
	 * @param newQuality a qualidade do thumbnail
	 * @return um array de bytes do thumbnail.
	 * @throws FileNotFoundException Se não encontrar a imagem a ser gerada.
	 */
	public static byte[] makeThumbnail(byte[] inFile, int newWidth,
			int newHeight, int newQuality) throws FileNotFoundException {

		// load image from INFILE
		Image image = Toolkit.getDefaultToolkit().createImage(inFile);
		MediaTracker mediaTracker = new MediaTracker(new Container());
		mediaTracker.addImage(image, 0);
		try {
			mediaTracker.waitForID(0);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		// determine thumbnail size from WIDTH and HEIGHT
		int thumbWidth = newWidth;
		int thumbHeight = newHeight;
		double thumbRatio = (double) thumbWidth / (double) thumbHeight;
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		double imageRatio = (double) imageWidth / (double) imageHeight;
		if (thumbRatio < imageRatio) {
			thumbHeight = (int) (thumbWidth / imageRatio);
		} else {
			thumbWidth = (int) (thumbHeight * imageRatio);
		}
		
		// draw original image to thumbnail image object and
		// scale it to the new size on-the-fly
		BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);

		// save thumbnail image to OUTFILE		
		ByteArrayOutputStream out = null;
		out = new ByteArrayOutputStream();		
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(thumbImage);
		int quality = newQuality;
		quality = Math.max(0, Math.min(quality, 100));
		param.setQuality((float) quality / 100.0f, false);
		encoder.setJPEGEncodeParam(param);
		byte[] byteArray = null  ;
		try {
			encoder.encode(thumbImage);
			byteArray = out.toByteArray();
			out.close();
		} catch (ImageFormatException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		return byteArray;
	}
	
	/**
	 * Retorna a dimensão de uma imagem qualquer.
	 * @param inFile a imagem em forma de array de bytes.
	 * @return Dimension a dimensão.
	 */
	public static Dimension getDimencao(byte[] inFile){
		Image image = Toolkit.getDefaultToolkit().createImage(inFile);		
		MediaTracker mediaTracker = new MediaTracker(new Container());
		mediaTracker.addImage(image, 0);
		try {
			mediaTracker.waitForID(0);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return new Dimension(image.getWidth(null), image.getHeight(null));
	}

	
	public static void main(String[] args) throws Exception {
		if (args.length != 5) {
			System.err.println("Usage: java Thumbnail INFILE "
					+ "OUTFILE WIDTH HEIGHT QUALITY");
			System.exit(1);
		}
		
		
	}

}