import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Iris {
	private ImagePanel imagePane;
	private Mat visuimg;
	private String fullFileName;
	private Segmentation inner;
	private Segmentation outer;
	private boolean heterochromia;
	private boolean freckles;
	private boolean fibrills;
	
	public Iris() {
		super();
		inner = new Segmentation();
		outer = new Segmentation();
		heterochromia = false;
		freckles = false;
		fibrills = false;
	}

	public Segmentation getInner() {
		return inner;
	}

	public Segmentation getOuter() {
		return outer;
	}
		
	public void setImage(String fullfname) {
		fullFileName = fullfname;
		inner.setImage(fullfname);
		outer.setImage(fullfname);
		visuimg = inner.getImgorig();
	}

	public Mat getVisuimg() {		
		return visuimg;
	}

	public Mat getVisuimgCloneWithCircles(double sc) {
		Mat vimg = visuimg.clone();
		Size sz = new Size(visuimg.width()*sc,visuimg.height()*sc);
		Imgproc.resize( vimg, vimg, sz );

		inner.drawCircle(vimg, 255);
		outer.drawCircle(vimg, 0);
		return vimg;
	}

	public void export_iris(String fullOutName) {
		/* write out files */
		
		/* write out iris, inner as well as outer ring */
		if (inner.getR() > 0 && outer.getR() > 0 && inner.getR() < outer.getR()) {
			Mat img = inner.getImgorig();
			Mat segf = new Mat(img.height(), img.width(), img.type());
			Mat segi = new Mat(img.height(), img.width(), img.type());
			Mat sego = new Mat(img.height(), img.width(), img.type());

			int X1 = (int) ( (double) inner.getX() / inner.getScale());
			int Y1 = (int) ( (double) inner.getY() / inner.getScale());
			int R1 = (int) ( (double) inner.getR() / inner.getScale());

			int X2 = (int) ( (double) outer.getX() / outer.getScale());
			int Y2 = (int) ( (double) outer.getY() / outer.getScale());
			int R2 = (int) ( (double) outer.getR() / outer.getScale());
			
			int Ri = R1 + (R2-R1) / 2;
			int Ro = R2 - (R2-R1) / 2;
			double[] blk = {0,0,0};
			for (int i = 0; i< img.height(); i++) {
				for (int j = 0; j< img.width(); j++) {
					double dr1 = Math.sqrt(Math.pow((double) j-X1,2.0) + Math.pow((double) i-Y1,2.0));
					double dr2 = Math.sqrt(Math.pow((double) j-X2,2.0) + Math.pow((double) i-Y2,2.0));
					double[] data = img.get(i, j);
					if (dr1 >= R1 && dr2 <= R2) {
						segf.put(i, j, data);
					} else {
						segf.put(i, j, blk);
					}
					
					if (dr1 >= R1 && dr1 <= Ri) {
						segi.put(i, j, data);
					} else {
						segi.put(i, j, blk);
					}
					
					if (dr2 >= Ro && dr2 <= R2) {
						sego.put(i, j, data);
					} else {
						sego.put(i, j, blk);
					}
				} 
			}

			try {
				String filename = fullOutName.replaceAll(".jpg","").replaceAll(".JPG","").replaceAll(".png","") + "_iris.png";;
				ImageIO.write(Imgtools.mattobitmap(segf), "png", new File(filename));
				filename = fullOutName.replaceAll(".jpg","").replaceAll(".JPG","").replaceAll(".png","") + "_inner_iris.png";;
				ImageIO.write(Imgtools.mattobitmap(segi), "png", new File(filename));
				filename = fullOutName.replaceAll(".jpg","").replaceAll(".JPG","").replaceAll(".png","") + "_outer_iris.png";;
				ImageIO.write(Imgtools.mattobitmap(sego), "png", new File(filename));
			} catch (Exception ex) {
				System.out.println("Problem writing out files");
			}
		}
	}

	public void writeParamFile(String fname) {
		
		try{
			FileWriter fstream = new FileWriter(fname);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(this.heterochromia + "\n");
			out.write(this.fibrills + "\n");
			out.write(this.freckles + "\n");
			out.close();
		} catch (Exception ex) {
		}
	}

	public void loadParamFile(String fname) {
		try{
			BufferedReader br = new BufferedReader(new FileReader(fname));
			String ln = br.readLine();
			this.heterochromia = Boolean.parseBoolean(ln); ln = br.readLine();
			this.fibrills = Boolean.parseBoolean(ln); ln = br.readLine();
			this.freckles = Boolean.parseBoolean(ln); ln = br.readLine();
			br.close();
		} catch (Exception ex) {
		}
	}

	public boolean isHeterochromia() {
		return heterochromia;
	}

	public void setHeterochromia(boolean heterochromia) {
		this.heterochromia = heterochromia;
	}

	public boolean isFreckles() {
		return freckles;
	}

	public void setFreckles(boolean freckles) {
		this.freckles = freckles;
	}

	public boolean isFibrills() {
		return fibrills;
	}

	public void setFibrills(boolean fibrills) {
		this.fibrills = fibrills;
	}

}
