import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author andreas
 *
 */
public class Segmentation {

	private Mat img;
	private Mat imgorig;
	private Mat colimg;
	private Mat grey;
	private Mat edges;
	private int r;
	private int x;
	private int y;
	private double light;
	private double ks;
	private double std;
	private double canny;
	private double accum;
	private double dp;
	private double minD;
	private int r1;
	private int r2;
	private double scale;
	
	public void setImage(String fname) {
		try {
			System.out.println("set Image " + fname);
			this.imgorig = Imgtools.bitmaptomat(ImageIO.read(new File(fname)));
			img = new Mat();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		scale = 300.0 / imgorig.height();

		Size sz = new Size(imgorig.width()*scale,imgorig.height()*scale);
		Imgproc.resize( imgorig, img, sz );	
	}

	public void setParams(double light, double ks, double std, double canny, double accum, double dp, double minD, int r1, int r2) {
		this.light = light;
		this.ks = ks;
		this.std = std;
		this.canny = canny;
		this.accum = accum;
		this.dp = dp;
		this.minD = minD;
		this.r1 = r1;
		this.r2 = r2;
	}

	public void exract_radius() {
		/* grey image */ 
		colimg = img;
		grey = new Mat (img.width(), img.height(), CvType.CV_8UC1);
		edges = new Mat (img.width(), img.height(), CvType.CV_8UC1);

		Imgproc.cvtColor(colimg, grey, Imgproc.COLOR_BGR2GRAY); //org.opencv.imgproc.*

		/* add light */
		Core.add(grey, new Scalar(light), grey);

		Imgproc.erode(grey, grey, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5)));        

		Imgproc.dilate(grey, grey, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)));

		/* detect and plot circle version 1*/
		Imgproc.GaussianBlur(grey, grey, new Size(ks,ks), std, std);
		
		Imgproc.Canny(grey, edges, canny/2,canny);
		
		Mat circles = new Mat();
		Imgproc.HoughCircles(grey, circles, Imgproc.HOUGH_GRADIENT, dp, minD, canny, accum, r1, r2);

		/* distance to mid point */
		double[] dist = new double[circles.cols()];
		double mindist=grey.height()*grey.height();
		int idx=0;
		int x0=grey.rows()/2,y0=grey.cols()/2;

		for (int x = 0; x < circles.cols(); x++) {
			double vCircle[]=circles.get(0,x);
			Point center=new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
			dist[x] = Math.pow(vCircle[0]-x0,2)+Math.pow(vCircle[1]-y0,2.0);
			if (vCircle[2] >= r1 && vCircle[2] <= r2) {
				if (dist[x]<mindist) {
					mindist = dist[x];
					idx = x;
				}
			}
		}

		if (!circles.empty()) {
			idx = 0;
			double vCircle[]=circles.get(0,idx);
			Point center=new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
			this.r = (int) Math.round(vCircle[2]);
			this.x = (int) vCircle[0];
			this.y = (int) vCircle[1];
			
		}
		
	}
	
	/* this version does not do the radius extraction*/ 
	public void exract_imgset() {
		/* grey image */ 
		colimg = img;
		grey = new Mat (img.width(), img.height(), CvType.CV_8UC1);
		edges = new Mat (img.width(), img.height(), CvType.CV_8UC1);

		Imgproc.cvtColor(colimg, grey, Imgproc.COLOR_BGR2GRAY); //org.opencv.imgproc.*

		/* add light */
		Core.add(grey, new Scalar(light), grey);

		Imgproc.erode(grey, grey, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5)));        

		Imgproc.dilate(grey, grey, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)));

		/* detect and plot circle version 1*/
		Imgproc.GaussianBlur(grey, grey, new Size(ks,ks), std, std);
		
		Imgproc.Canny(grey, edges, canny/2,canny);
		
	}

	public void writeParamfile(String fname) {
		// TODO add your handling code here:
		try{
			FileWriter fstream = new FileWriter(fname);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(this.r + "\n");
			out.write(this.x + "\n");
			out.write(this.y + "\n");
			out.write(this.light + "\n");
			out.write(this.ks + "\n");
			out.write(this.std + "\n");
			out.write(this.canny + "\n");
			out.write(this.accum + "\n");
			out.write(this.dp + "\n");
			out.write(this.minD + "\n");
			out.write(this.r1 + "\n");
			out.write(this.r2 + "\n");
			out.close();
		} catch (Exception ex) {
		}
	}

	public void setRadius(int x0, int y0, int r0) {
		this.r = r0;
		this.x = x0;
		this.y = y0;
	}

	/* draw into the scale of the given image */
	public void drawCircle(Mat img, int col3) {
		if ( this.r > 0) {
			double sc =  (double) this.colimg.height() / img.height();
			Point center=new Point(Math.round(this.x / sc), Math.round(this.y / sc));
			Imgproc.circle(img, center, (int) (r / sc), new Scalar(255,0,col3));
		}
	}

	public void loadParamfile(String fname) {
		try{
			BufferedReader br = new BufferedReader(new FileReader(fname));
			String ln = br.readLine();
			this.r = Integer.parseInt(ln); ln = br.readLine();
			this.x = Integer.parseInt(ln); ln = br.readLine();
			this.y = Integer.parseInt(ln); ln = br.readLine();
			this.light = Double.parseDouble(ln); ln = br.readLine();
			this.ks = Double.parseDouble(ln); ln = br.readLine();
			this.std = Double.parseDouble(ln); ln = br.readLine();
			this.canny = Double.parseDouble(ln); ln = br.readLine();
			this.accum = Double.parseDouble(ln); ln = br.readLine();
			this.dp = Double.parseDouble(ln); ln = br.readLine();
			this.minD = Double.parseDouble(ln); ln = br.readLine();
			this.r1 = Integer.parseInt(ln); ln = br.readLine();
			this.r2 = Integer.parseInt(ln); ln = br.readLine();
			br.close();
		} catch (Exception ex) {
		}
	}

	public int getR() {
		return r;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Mat getColimg() {
		return colimg;
	}

	public Mat getGrey() {
		return grey;
	}

	public Mat getEdges() {
		return edges;
	}

	public double getLight() {
		return light;
	}

	public double getKs() {
		return ks;
	}

	public double getStd() {
		return std;
	}

	public double getCanny() {
		return canny;
	}

	public double getAccum() {
		return accum;
	}

	public double getDp() {
		return dp;
	}

	public double getMinD() {
		return minD;
	}

	public int getR1() {
		return r1;
	}

	public int getR2() {
		return r2;
	}

	public double getScale() {
		return scale;
	}

	public Mat getImgorig() {
		return imgorig;
	}
	
	
	
}
