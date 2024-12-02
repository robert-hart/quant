import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JFrame;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class Imgtools {
	static Mat bitmaptomat(BufferedImage image) {
		byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, data);
		return mat;
	}

	static BufferedImage mattobitmap(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( m.channels() > 1 ) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels()*m.cols()*m.rows();
		byte [] b = new byte[bufferSize];
		m.get(0,0,b); // get all the pixels
		BufferedImage bimage = new BufferedImage(m.cols(),m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) bimage.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);  
		return bimage;
	}
	
	/* quickly show image in extra window */
	static void imshow(BufferedImage img) {
		JFrame frame = new JFrame("Painting Example");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ImagePanel imagePane = new ImagePanel(img);        
		frame.setContentPane(imagePane);	
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);	
	}

	/* quickly show image in extra window */
	static void imshow(Mat m) {
		BufferedImage img = Imgtools.mattobitmap(m);
		JFrame frame = new JFrame("Painting Example");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ImagePanel imagePane = new ImagePanel(img);        
		frame.setContentPane(imagePane);	
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);	
	}
}
