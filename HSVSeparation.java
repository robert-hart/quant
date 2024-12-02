import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class HSVSeparation {

	class point {
		point(double x, double y, int value)
		{
			this.x = x;
			this.y = y;
			this.value = value;
		}
		double x, y;
		int value;
	}

	private Vector<point> point_list;
	private svm_model model;

	private Mat sep1; /*H vs S*/
	private Mat sep2; /*S vs V*/
	private Mat sep3; /*H vs V*/
	int width;
	int cols[][]={{0,0,0},{255,255,0},{0,255,255},{0,0,255},{255,0,255},{0,255,0},{255,0,0},{128,128,0},{203,192,255},{0,140,255},{0,128,128}};
	
	private String qOutFile;

	public HSVSeparation() {
		width = 240;
		sep1 = new Mat(width,width,CvType.CV_8UC3);
		sep2 = new Mat(width,width,CvType.CV_8UC3);
		sep3 = new Mat(width,width,CvType.CV_8UC3);
		point_list = new Vector<point>();
		qOutFile = "quantificatio_values.tsv";
	}

	public void addClass(Mat img, int cl) {
		Mat imhsv = new Mat();
		Imgproc.cvtColor(img, imhsv, Imgproc.COLOR_RGB2HSV);
		double[] max = {0,0,0};
		for (int i = 0; i < img.height(); i++)
			for (int j = 0; j < img.width(); j++) {
				double[] data = imhsv.get(i, j);
				if (data[0]>max[0]) max[0] = data[0];
				if (data[1]>max[1]) max[1] = data[1];
				if (data[2]>max[2]) max[2] = data[2];
			}

		for (int i = 0; i < img.height(); i++)
			for (int j = 0; j < img.width(); j++) {
				double[] data = imhsv.get(i, j);
				double h = data[0]/max[0]*width;
				double s = data[1]/max[1]*width;
				double v = data[2]/max[2]*width;

				if (data[0]>0 && data[0]<max[0] && data[1]>0 && data[1]<max[1]) {
					point p = new point(data[0],data[1],cl); 
					point_list.addElement(p);
				}

				if (data[0]>max[0]) max[0] = data[0];
				if (data[1]>max[1]) max[1] = data[1];
				if (data[2]>max[2]) max[2] = data[2];

				data[0] = (double) cols[cl][0];
				data[1] = (double) cols[cl][1];
				data[2] = (double) cols[cl][2];

				sep1.put(width-(int) Math.round(s),(int) Math.round(h), data);
				sep2.put(width-(int) Math.round(v),(int) Math.round(s), data);
				sep3.put(width-(int) Math.round(v),(int) Math.round(h), data);
			}

		// test colors
		/* for (int c=0;c<11;c++)
			for (int i = 0; i < 10; i++)
				for (int j = 0; j < 10; j++) {
					double[] data={0,0,0};

					data[0] = (double) cols[c][0];
					data[1] = (double) cols[c][1];
					data[2] = (double) cols[c][2];

					sep1.put(width-(int) Math.round(10*c+i),(int) Math.round(10*c+j), data);
				} */
	}

	public void train_svm() {
		svm_parameter param = new svm_parameter();

		// default values
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.RBF;
		param.degree = 3;
		param.gamma = 0.5;
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 40;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 0; /*-h shrinking heuristic def 1*/
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];

		// build problem
		svm_problem prob = new svm_problem();
		prob.l = point_list.size();
		prob.y = new double[prob.l];


		prob.x = new svm_node [prob.l][2];
		for(int i=0;i<prob.l;i++)
		{
			point p = point_list.elementAt(i);
			prob.x[i][0] = new svm_node();
			prob.x[i][0].index = 1;
			prob.x[i][0].value = p.x;
			prob.x[i][1] = new svm_node();
			prob.x[i][1].index = 2;
			prob.x[i][1].value = p.y;
			prob.y[i] = p.value;
		}

		// build model & classify
		model = svm.svm_train(prob, param);
	}

	public void quantify_iris(String fname) {
		System.out.println("Quantifying " + fname);
		Mat imrgb = new Mat();
		try {
			imrgb  = Imgtools.bitmaptomat(ImageIO.read(new File(fname)));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		Mat imhsv = new Mat();
		Mat imquant = imrgb.clone();

		Imgproc.cvtColor(imrgb, imhsv, Imgproc.COLOR_RGB2HSV);
		int predcl[] = {0,0,0,0,0,0,0,0,0,0,0,0};
		svm_node[] x = new svm_node[2];
		x[0] = new svm_node();
		x[1] = new svm_node();
		x[0].index = 1;
		x[1].index = 2;

		for (int i = 0; i < imrgb.height(); i++)
			for (int j = 0; j < imrgb.width(); j++) {
				double[] data = imhsv.get(i, j);
				double[] rgb = imrgb.get(i, j);
				double h = data[0];
				double s = data[1];
				double v = data[2];

				if (rgb[0]>0 && rgb[1]>0 && rgb[2]>0 && rgb[0]<255 && rgb[1]<255 && rgb[2]<255) {

					x[0].value = h;
					x[1].value = s;
					double d = svm.svm_predict(model, x);
					predcl[(int) d]++;
					
					/* color respective pixel */
					rgb[0] = cols[(int) d][0];
					rgb[1] = cols[(int) d][1];
					rgb[2] = cols[(int) d][2];
					imquant.put(i, j, rgb);
				}

			}
		
		for (int i=0;i<predcl.length;i++) System.out.println("predclass " + i + " -> " + predcl[i] );
		
		/* append write to text file */
		
		File fl = new File(fname);
		String dir = fl.getAbsolutePath();
		dir = dir.substring(0, dir.lastIndexOf(File.separator));
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(dir + "/" + qOutFile, true)))) {
			out.print(fname+"\t");
		    for (int i=0;i<predcl.length;i++) out.print(predcl[i] + "\t");
		    out.println();
		    out.close();
		}catch (IOException e) {
		    
		}
		
		/* out quantified iris */
		try {
			String filename = fname.replaceAll(".jpg","").replaceAll(".JPG","").replaceAll(".png","").replaceAll(".PNG","") + "_cat.png";;
			ImageIO.write(Imgtools.mattobitmap(imquant), "png", new File(filename));
		} catch (Exception ex) {
		}
	}
	
	public void initQuantificationFile(String pathToFile) {
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(pathToFile + "/" + qOutFile, false)))) {
		    out.println();
		    out.close();
		}catch (IOException e) {
		    
		}
	}

	public Mat getSep1() {
		return sep1;
	}

	public Mat getSep2() {
		return sep2;
	}

	public Mat getSep3() {
		return sep3;
	}


}
