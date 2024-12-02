import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

class ImagePanel extends JPanel {

    private BufferedImage image;

      public void setImage(Mat m, double scale) {
    	
    	/* resize to current size */
    	Size sz = new Size(m.width()*scale,m.height()*scale);
    	Imgproc.resize( m, m, sz );
    	
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
		image =  bimage;
	}

	public ImagePanel(BufferedImage img) {
        setOpaque(true);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
		image = img;
    }
	
	public ImagePanel() {
        setOpaque(true);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
		image = null;
    }

	public ImagePanel(Mat m) {
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
		image =  bimage;
    }
	
    @Override
    public Dimension getPreferredSize() {
    	if (image != null) 
    		return (new Dimension(image.getWidth(), image.getHeight()));
    	return (new Dimension(0,0));
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) g.drawImage(image, 0, 0, this);
    }
}