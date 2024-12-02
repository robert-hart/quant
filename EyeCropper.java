import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class EyeCropper {

	private String absoluteOutputPath;

	public EyeCropper() {
		super();
		absoluteOutputPath = "";
	}

	public void setAbsoluteOutputPath(String absoluteOutputPath) {
		this.absoluteOutputPath = absoluteOutputPath;
	}

	public void parseFile(String fname) {
		try {

			CascadeClassifier eyeDetector = new CascadeClassifier();
			Boolean loaded = eyeDetector.load("/usr/local/share/OpenCV/haarcascades/haarcascade_eye.xml");
			if (loaded) System.out.println("\nDetector loaded");

			File fn = new File(fname);
			Mat image = Imgtools.bitmaptomat(ImageIO.read(fn));
			Mat imgsc = new Mat(); 

			double scale = 500.0 / image.height();

			Size sz = new Size(image.width()*scale,image.height()*scale);
			Imgproc.resize( image, imgsc, sz );

			MatOfRect detections = new MatOfRect();

			eyeDetector.detectMultiScale(imgsc, detections);

			System.out.println(String.format("Detected %s objects in " + fname, detections.toArray().length));
			int k=0, idx = 0, x0 = imgsc.height()/2, y0=imgsc.width()/2;

			double dist = 1000000;
			/* get the eye closest to middle */
			for (Rect rect : detections.toArray()) {
				double d = Math.pow(rect.x + rect.height/2 - x0,2) + Math.pow(rect.y + rect.width/2 - y0,2);
				if (d < dist) {
					idx = k;
					dist = d;
				}
				k++;
			}

			Rect rect = detections.toArray()[idx];
			rect.x /= scale;
			rect.y /= scale;
			rect.width /= scale;
			rect.height /= scale;

			Mat subimage = image.submat(rect);
			if (absoluteOutputPath=="") absoluteOutputPath = fn.getPath();

			String filename = absoluteOutputPath + fn.getName().replaceAll(".jpg","").replaceAll(".JPG","") + "_eye.png";
			System.out.println(String.format("Writing %s", filename));
			ImageIO.write(Imgtools.mattobitmap(subimage), "png", new File(filename));
			k++;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void parseFolder(String dirname) {
		File folder = new File(dirname);
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isFile()) {
				if (fileEntry.getName().endsWith("jpg") || fileEntry.getName().endsWith("JPG")) {
					parseFile(fileEntry.getAbsolutePath());
				}
			} 
		}
	}

	public static void main(String[] args) {
		/* we need an input and output path */
		System.out.println("EyeCropper");
		System.out.println("Detect eye on portraits and save surrounding region.");
		System.out.println("(c) andreas.wollstein@gmail.com");
		System.out.println("Usage: java -jar eyecropper.jar <inputdir with jpg images> <outputdir>");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		System.out.println("inputdir : " + args[0]);
		System.out.println("outputdir: " + args[1]);

		EyeCropper ec = new EyeCropper();	
		ec.setAbsoluteOutputPath(args[1]);
		ec.parseFolder(args[0]);
		
	}
}
